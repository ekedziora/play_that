package controllers

import authorization.AuthorizeEventByOwner
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import forms.{AddEventForm, ListFiltersForm, ParticipantsPresenceForm}
import models.{User, Username}
import play.api.cache.CacheApi
import play.api.data.Form
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import service.EventService
import utils._

import scala.concurrent.Future

class EventController @Inject() (val messagesApi: MessagesApi, val env: Environment[User, CookieAuthenticator],
                                 val eventService: EventService, val cache: CacheApi)
  extends Silhouette[User, CookieAuthenticator] {

  def showAddEventPage = SecuredAction.async { implicit request =>
    eventService.getDisciplineOptions.map { optionsSeq =>
      Ok(views.html.addEvent(AddEventForm.form, optionsSeq, request.identity))
    }
  }

  def saveNewEvent = SecuredAction.async { implicit request =>
    AddEventForm.form.bindFromRequest.fold(
      formWithErrors => {
        var form = formWithErrors
        if (formWithErrors.error("lat").isDefined || formWithErrors.error("lng").isDefined) {
          form = formWithErrors.withError("address", Messages("string.not.represents.address"))
        }
        eventService.getDisciplineOptions.map { optionsSeq =>
          BadRequest(views.html.addEvent(form, optionsSeq, request.identity))
        }
      },
      data => {
        eventService.saveNewEvent(data, request.identity.userID).map { eventId =>
          Redirect(routes.EventController.showEventDetails(eventId)).flashing(ViewUtils.InfoFlashKey -> Messages("event.added"))
        }
      }
    )
  }

  def showEventDetails(eventId: Long) = SecuredAction.async { implicit request =>
    eventService.getEventWithParticipants(eventId).map { event =>
      Ok(views.html.eventDetails(event, request.identity))
    } recoverWith {
      case nfe: NotFoundException => Future.successful(ControllerUtils.getDefaultNotFoundResponse)
    }
  }

  def showEditEventPage(eventId: Long) = SecuredAction(AuthorizeEventByOwner(eventId, eventService)).async { implicit request =>
    val eventDetailsFuture = eventService.getEventDetails(eventId)
    val disciplineOptionsFuture = eventService.getDisciplineOptions

    (for {
      event <- eventDetailsFuture
      options <- disciplineOptionsFuture
    } yield {
      val form = AddEventForm.form.fill(new AddEventForm.Data(event))
      Ok(views.html.editEvent(eventId, form, options, request.identity))
    }).recoverWith {
      case nfe: NotFoundException => Future.successful(ControllerUtils.getDefaultNotFoundResponse)
    }
  }

  def updateEvent(eventId: Long) = SecuredAction(AuthorizeEventByOwner(eventId, eventService)).async { implicit request =>
    AddEventForm.form.bindFromRequest.fold(
      formWithErrors => {
        eventService.getDisciplineOptions.map { optionsSeq =>
          BadRequest(views.html.editEvent(eventId, formWithErrors, optionsSeq, request.identity))
        }
      },
      data => {
        eventService.updateEvent(eventId, data).map { updatedCount =>
          Redirect(routes.EventController.showEventDetails(eventId)).flashing(ViewUtils.InfoFlashKey -> Messages("event.updated"))
        }.recoverWith {
          case ve: ValidationException =>
            eventService.getDisciplineOptions.flatMap { options =>
              val form = AddEventForm.form.fill(data).withGlobalError(ve.getMessageForView)
              Future.successful(BadRequest(views.html.editEvent(eventId, form, options, request.identity)))
            }
        }
      }
    )
  }

  def showList(cachedFilters: Boolean) = SecuredAction.async { implicit request =>
    var form = ListFiltersForm.form.bindFromRequest
    if (cachedFilters) {
      cache.get[Form[ListFiltersForm.Data]](CacheUtils.EventListFiltersKey).foreach(form = _)
    } else {
      cache.set(CacheUtils.EventListFiltersKey, form)
    }

    form.fold (
      formWithErrors => {
        eventService.getDisciplineOptionsForFilter.map { optionsSeq =>
          BadRequest(views.html.eventsList(Seq(), formWithErrors, optionsSeq, request.identity))
        }
      },
      data => {
        for {
          options <- eventService.getDisciplineOptionsForFilter
          events <- eventService.getEventsList(data)
        } yield {
          Ok(views.html.eventsList(events, ListFiltersForm.form.fill(data), options, request.identity))
        }
      }
    )
  }

  def deleteEvent(eventId: Long) = SecuredAction(AuthorizeEventByOwner(eventId, eventService)).async { implicit request =>
    eventService.deleteEvent(eventId)
    Future.successful(Redirect(routes.EventController.showList()).flashing(ViewUtils.InfoFlashKey -> Messages("event.deleted")))
  }

  def joinEvent(eventId: Long) = SecuredAction.async { implicit request =>
    eventService.addParticipant(eventId, request.identity.userID).flatMap { wasAdded =>
      if (wasAdded) {
        Future.successful(Redirect(routes.EventController.showEventDetails(eventId)))
      } else {
        Future.successful(Redirect(routes.EventController.showEventDetails(eventId)).flashing(ViewUtils.ErrorFlashKey -> Messages("event.not.joined")))
      }
    }
  }

  def unjoinEvent(eventId: Long) = SecuredAction.async {implicit request =>
    eventService.removeParticipant(eventId, request.identity.userID).flatMap { wasRemoved =>
      if (wasRemoved) {
        Future.successful(Redirect(routes.EventController.showEventDetails(eventId)))
      } else {
        Future.successful(Redirect(routes.EventController.showEventDetails(eventId)).flashing(ViewUtils.ErrorFlashKey -> Messages("event.not.unjoined")))
      }
    }
  }

  def showPresenceReportPage(eventId: Long) = SecuredAction(AuthorizeEventByOwner(eventId, eventService)).async { implicit request =>
    eventService.getParticipantsPresence(eventId).map { participantsPresence =>
      val presenceList = participantsPresence.map { presence =>
        ParticipantsPresenceForm.ParticipantPresence(presence.userId, presence.present)
      }.toList

      val userIdToNameMap = participantsPresence.map { presence =>
        presence.userId -> Username(presence.fullName, presence.username)
      }.toMap

      val form = ParticipantsPresenceForm.form.fill(ParticipantsPresenceForm.Data(presenceList))
      Ok(views.html.presenceReport(eventId, form, userIdToNameMap, request.identity))
    }
  }

  def savePresenceReport(eventId: Long) = SecuredAction(AuthorizeEventByOwner(eventId, eventService)).async { implicit request =>
    ParticipantsPresenceForm.form.bindFromRequest().fold(
      formWithErrors => Future.successful(Redirect(routes.EventController.showPresenceReportPage(eventId))),
      data => eventService.savePresenceReport(eventId, data.participantsPresence).map { updatedParticipantsCount =>
        Redirect(routes.EventController.showEventDetails(eventId)).flashing(ViewUtils.InfoFlashKey -> Messages("presence.report.saved"))
      }
    )
  }

}
