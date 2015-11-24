package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import models.User
import play.api.i18n.MessagesApi

class UserAccountController @Inject() (
    val messagesApi: MessagesApi,
    val env: Environment[User, CookieAuthenticator],
    socialProviderRegistry: SocialProviderRegistry)
  extends Silhouette[User, CookieAuthenticator] {

}
