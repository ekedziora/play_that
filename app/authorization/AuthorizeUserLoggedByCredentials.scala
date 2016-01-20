package authorization

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.User
import play.api.i18n.Messages
import play.api.mvc.Request

import scala.concurrent.Future

case object AuthorizeUserLoggedByCredentials extends Authorization[models.User, CookieAuthenticator] {
  override def isAuthorized[B](identity: User, authenticator: CookieAuthenticator)(implicit request: Request[B], messages: Messages): Future[Boolean] = {
    Future.successful(identity.loginInfo.providerID == CredentialsProvider.ID)
  }
}
