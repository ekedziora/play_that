package models.services

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{PasswordHasher, PasswordInfo}
import com.mohiva.play.silhouette.api.{AuthInfo, LoginInfo}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.AccountDetailsEditForm.Data
import forms.ChangePasswordForm
import models.daos.UserDAO
import models.{Gender, User}
import play.api.libs.concurrent.Execution.Implicits._
import provider.CustomSocialProfile
import utils.{DateTimeUtils, ValidationException}

import scala.concurrent.Future
import scala.util.Random

/**
 * Handles actions to users.
 *
 * @param userDAO The user DAO implementation.
 */
class UserServiceImpl @Inject() (userDAO: UserDAO, passwordHasher: PasswordHasher, authInfoRepository: AuthInfoRepository)
  extends UserService {

  override def getById(id: UUID): Future[Option[User]] = {
    userDAO.find(id)
  }

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDAO.find(loginInfo)

  override def changePassword(data: ChangePasswordForm.Data, userId: UUID): Future[AuthInfo] = {
    userDAO.findLoginInfoByUserIdAndProviderId(userId, CredentialsProvider.ID).flatMap { loginInfo =>
      authInfoRepository.find[PasswordInfo](loginInfo).flatMap { optionPasswordInfo =>
        if (optionPasswordInfo.isDefined && passwordHasher.matches(optionPasswordInfo.get, data.oldPassword)) {
          authInfoRepository.update(loginInfo, passwordHasher.hash(data.newPassword))
        } else {
          Future.failed(ValidationException.createWithValidationMessageKey("password.incorrect"))
        }
      }
    }
  }

  override def updateAccountDetails(accountData: Data, userId: UUID): Future[Int] = {
    userDAO.findDuplicatedUsername(accountData.username, Some(userId)).flatMap {
      case true => Future.failed(ValidationException.createWithValidationMessageKey("username.exists"))
      case false => userDAO.updateUserAccount(accountData, userId)
    }
  }

  override def findDuplicatedUsername(username: String) : Future[Boolean] = userDAO.findDuplicatedUsername(username, None)

  override def saveNewUser(user: User) = {
    userDAO.findDuplicatedUsername(user.username, None).flatMap {
      case true => Future.failed(ValidationException.createWithValidationMessageKey("username.exists"))
      case false => userDAO.save(user)
    }
  }

  override def updateUser(user: User): Future[User] = {
    userDAO.save(user)
  }

  override def saveOrUpdateUser(profile: CustomSocialProfile): Future[(User, Boolean)] = {
    userDAO.find(profile.loginInfo).flatMap {
      case Some(user) => // Update user with profile
        userDAO.save(user.copy(
          firstName = profile.firstName,
          lastName = profile.lastName,
          email = profile.email,
          avatarURL = profile.avatarURL,
          birthDate = profile.birthday.flatMap(DateTimeUtils.parseLocalDate),
          gender = profile.gender.flatMap(Gender.fromProfile)
        )).map(user => (user, false))
      case None => // Insert a new user
        (for {
          username <- createRandomUniqueUsername
          user <- userDAO.save(User(
            userID = UUID.randomUUID(),
            loginInfo = profile.loginInfo,
            username = username,
            firstName = profile.firstName,
            lastName = profile.lastName,
            email = profile.email,
            avatarURL = profile.avatarURL,
            birthDate = profile.birthday.flatMap(DateTimeUtils.parseLocalDate),
            gender = profile.gender.flatMap(Gender.fromProfile)
          ))
        } yield user)
          .map { user => (user, true)}
    }
  }

  override def createRandomUniqueUsername = {
    userDAO.getAllUsernames.map { usernames =>
        var randomString = ""
        do {
          randomString = Random.alphanumeric.take(10).mkString
        } while (usernames.contains(randomString))
        randomString
    }
  }
}
