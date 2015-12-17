package controllers

import authorization.AuthorizeEventByOwner
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import forms.AddEventForm
import models.User
import models.services.EventService
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import utils.NotFoundException

import scala.concurrent.Future

class EventController @Inject() (val messagesApi: MessagesApi, val env: Environment[User, CookieAuthenticator], val eventService: EventService)
  extends Silhouette[User, CookieAuthenticator] {

  def showAddEventPage = SecuredAction.async { implicit request =>
    eventService.getDisciplineOptions.map { optionsSeq =>
      Ok(views.html.addEvent(AddEventForm.form, optionsSeq, request.identity))
    }
  }

  def saveNewEvent = SecuredAction.async { implicit request =>
    AddEventForm.form.bindFromRequest.fold(
      formWithErrors => {
        eventService.getDisciplineOptions.map { optionsSeq =>
          BadRequest(views.html.addEvent(formWithErrors, optionsSeq, request.identity))
        }
      },
      data => {
        eventService.saveNewEvent(data, request.identity.userID).map { eventId =>
          Redirect(routes.EventController.showEventDetails(eventId)).flashing("info" -> Messages("event.added"))
        }
      }
    )
  }

  def showEventDetails(eventId: Long) = SecuredAction(AuthorizeEventByOwner(eventId, eventService)).async { implicit request =>
    eventService.getEventDetails(eventId).map { event =>
      Ok(views.html.eventDetails(event, request.identity))
    } recoverWith {
      PartialFunction {
        case nfe: NotFoundException => Future.successful(NotFound)
      }
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
      PartialFunction {
        case nfe: NotFoundException => Future.successful(NotFound)
      }
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
          Redirect(routes.EventController.showEventDetails(eventId)).flashing("info" -> Messages("event.updated"))
        }
      }
    )
  }
}