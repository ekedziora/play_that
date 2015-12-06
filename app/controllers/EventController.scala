package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import forms.AddEventForm
import models.User
import models.services.EventService
import play.api.i18n.MessagesApi
import play.api.libs.concurrent.Execution.Implicits._

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
        eventService.saveNewEvent(data).map { eventId =>
          // redirect do strony ze szczegółami eventu + flashing
          Redirect(routes.ApplicationController.index())
        }
      }
    )
  }

}
