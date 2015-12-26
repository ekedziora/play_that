package authorization

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.User
import models.services.EventService
import play.api.i18n.Messages
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Request

import scala.concurrent.Future

case class AuthorizeEventByOwner (eventId: Long, eventService: EventService) extends Authorization[models.User, CookieAuthenticator] {
  override def isAuthorized[B](identity: User, authenticator: CookieAuthenticator)(implicit request: Request[B], messages: Messages): Future[Boolean] = {
    eventService.getEventOwnerId(eventId).map { optionalOwnerId =>
      optionalOwnerId.contains(identity.userID)
    }
  }
}
