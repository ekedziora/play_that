package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import forms.AccountDetailsEditForm.Data
import forms.{AccountDetailsEditForm, ChangePasswordForm}
import models.User
import models.services.UserService
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import utils.{ValidationException, ViewUtils}

import scala.concurrent.Future

class UserAccountController @Inject() (
    userService: UserService,
    val messagesApi: MessagesApi,
    val env: Environment[User, CookieAuthenticator],
    socialProviderRegistry: SocialProviderRegistry)
  extends Silhouette[User, CookieAuthenticator] {

  def showAccountDetails = SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.accountDetails(request.identity)))
  }

  def editAccountDetails = SecuredAction.async { implicit request =>
    val form = AccountDetailsEditForm.form.fill(new Data(request.identity))
    Future.successful(Ok(views.html.accountDetailsEdit(form, request.identity)))
  }

  def saveAccountDetails = SecuredAction.async { implicit request =>
    AccountDetailsEditForm.form.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.accountDetailsEdit(formWithErrors, request.identity))),
      data => {
        userService.updateAccountDetails(data, request.identity.userID).map { status =>
          Redirect(routes.UserAccountController.showAccountDetails).flashing(ViewUtils.InfoFlashKey -> Messages("account.details.updated"))
        }.recoverWith {
          PartialFunction {
            case ve: ValidationException =>
              val form = AccountDetailsEditForm.form.fill(data).withGlobalError(ve.getMessageForView)
              Future.successful(BadRequest(views.html.accountDetailsEdit(form, request.identity)))
          }
        }
      }
    )
  }

  def showChangePasswordPage = SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.changePassword(ChangePasswordForm.form, request.identity)))
  }

  def changePassword = SecuredAction.async { implicit request =>
    ChangePasswordForm.form.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.changePassword(formWithErrors, request.identity))),
      data =>
        userService.changePassword(data, request.identity.userID).map { status =>
          Redirect(routes.UserAccountController.showAccountDetails).flashing(ViewUtils.InfoFlashKey -> Messages("password.changed"))
        }.recoverWith {
          PartialFunction {
            case ve: ValidationException =>
              val form = ChangePasswordForm.form.fill(data).withGlobalError(ve.getMessageForView)
              Future.successful(BadRequest(views.html.changePassword(form, request.identity)))
          }
        }
    )
  }

}
