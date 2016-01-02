package controllers

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import forms.SignUpForm
import models.services.{MailTokenService, UserService}
import models.{User, UserMailToken}
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Action
import utils._

import scala.concurrent.Future

/**
 * The sign up controller.
 *
 * @param messagesApi The Play messages API.
 * @param env The Silhouette environment.
 * @param userService The user service implementation.
 * @param authInfoRepository The auth info repository implementation.
 * @param avatarService The avatar service implementation.
 * @param passwordHasher The password hasher implementation.
 */
class SignUpController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator],
  userService: UserService,
  mailService: MailService,
  mailTokenService: MailTokenService,
  authInfoRepository: AuthInfoRepository,
  avatarService: AvatarService,
  passwordHasher: PasswordHasher)
  extends Silhouette[User, CookieAuthenticator] {

  implicit val ms = mailService

  def startSignUp = Action.async { implicit request =>
    SignUpForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.signUp(form))),
      data => {
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)

        userService.findByEmail(data.email).flatMap {
          case Some(_) =>
            val form = SignUpForm.form.fill(data).withGlobalError(Messages("user.exists"))
            Future.successful(BadRequest(views.html.signUp(form)))
          case None =>
            val authInfo = passwordHasher.hash(data.password)
            val user = User (
              userID = UUID.randomUUID(),
              loginInfo = loginInfo,
              username = data.username,
              email = data.email,
              avatarURL = None
            )

            (for {
              avatar <- avatarService.retrieveURL(data.email)
              user <- userService.saveNewUser(user.copy(avatarURL = avatar))
              authInfo <- authInfoRepository.add(loginInfo, authInfo)
              tokenId <- mailTokenService.create(UserMailToken(user.userID, isSignUp = true))
            } yield {
              Mailer.welcome(user, link = routes.SignUpController.signUp(tokenId).absoluteURL())
              Ok(views.html.almostSignedUp(user))
            }).recoverWith {
              PartialFunction {
                case ve: ValidationException =>
                  val form = SignUpForm.form.fill(data).withGlobalError(ve.getMessageForView)
                  Future.successful(BadRequest(views.html.signUp(form)))
              }
            }
        }
      }
    )
  }

  def signUp(tokenId: UUID) = Action.async { implicit request =>
        mailTokenService.retrieve(tokenId).flatMap {
          case Some(token) if token.isSignUp && !token.isExpired =>
            userService.getById(token.userId).flatMap {
              case Some(user) =>
                val loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
                env.authenticatorService.create(loginInfo).flatMap { authenticator =>
                  if (!user.emailConfirmed) {
                    userService.updateUser(user.copy(emailConfirmed = true)).map { newUser =>
                      env.eventBus.publish(SignUpEvent(newUser, request, request2Messages))
                    }
                  }
                  for {
                    cookie <- env.authenticatorService.init(authenticator)
                    result <- env.authenticatorService.embed(cookie, Redirect(routes.ApplicationController.index()).flashing(ViewUtils.InfoFlashKey -> Messages("signup.ready")))
                  } yield {
                    mailTokenService.consume(tokenId)
                    env.eventBus.publish(LoginEvent(user, request, request2Messages))
                    result
                  }
                }
              case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
            }
          case Some(token) =>
            mailTokenService.consume(tokenId)
            Future.successful(ControllerUtils.getDefaultNotFoundResponse)
          case None => Future.successful(ControllerUtils.getDefaultNotFoundResponse)
        }
    }
}
