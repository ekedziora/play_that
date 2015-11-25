package models.services

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import forms.AccountDetailsEditForm.Data
import models.User
import models.daos.UserDAO
import play.api.libs.concurrent.Execution.Implicits._
import utils.ValidationException

import scala.concurrent.Future

/**
 * Handles actions to users.
 *
 * @param userDAO The user DAO implementation.
 */
class UserServiceImpl @Inject() (userDAO: UserDAO) extends UserService {

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDAO.find(loginInfo)

  override def updateAccountDetails(accountData: Data): Future[Int] = {
    userDAO.findDuplicatedUsername(Option(accountData.username), Some(accountData.userId)).flatMap {
      case true => Future.failed(ValidationException.createWithValidationMessageKey("username.exists"))
      case false => userDAO.updateUserAccount(accountData)
    }
  }

  override def findDuplicatedUsername(username: Option[String]) : Future[Boolean] = userDAO.findDuplicatedUsername(username, None)

  override def save(user: User) = {
    userDAO.findDuplicatedUsername(user.username, None).flatMap {
      case true => Future.failed(ValidationException.createWithValidationMessageKey("username.exists"))
      case false => userDAO.find(user.loginInfo).flatMap { userOption =>
        if (userOption.isDefined)
          Future.failed(ValidationException.createWithValidationMessageKey("user.exists"))
        else
          userDAO.save(user)
      }
    }
  }

  override def save(profile: CommonSocialProfile) = {
    userDAO.find(profile.loginInfo).flatMap {
      case Some(user) => // Update user with profile
        userDAO.save(user.copy(
          firstName = profile.firstName,
          lastName = profile.lastName,
          email = profile.email,
          avatarURL = profile.avatarURL
        ))
      case None => // Insert a new user
        userDAO.save(User(
          userID = UUID.randomUUID(),
          loginInfo = profile.loginInfo,
          firstName = profile.firstName,
          lastName = profile.lastName,
          email = profile.email,
          avatarURL = profile.avatarURL
        ))
    }
  }
}
