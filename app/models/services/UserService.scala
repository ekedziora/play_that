package models.services

import java.util.UUID

import com.mohiva.play.silhouette.api.AuthInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import forms.{AccountDetailsEditForm, ChangePasswordForm}
import models.User
import provider.CustomSocialProfile

import scala.concurrent.Future

/**
 * Handles actions to users.
 */
trait UserService extends IdentityService[User] {

  def getById(id: UUID): Future[Option[User]]

  /**
   * Changes user password
   *
   * @param data change password data
   * @return new password info
   */
  def changePassword(data: ChangePasswordForm.Data, userId: UUID): Future[AuthInfo]

  /**
   * Updates user account data
   *
   * @param accountData user account data to update
   * @return status of operation
   */
  def updateAccountDetails(accountData: AccountDetailsEditForm.Data, userId: UUID) : Future[Int]

  /**
   * Checks if there exists user with given username
   *
   * @param username username to check
   * @return true if user with username exists, false otherwise
   */
  def findDuplicatedUsername(username: String): Future[Boolean]

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def saveNewUser(user: User): Future[User]

  def updateUser(user: User): Future[User]

  /**
   * Saves the social profile for a user.
   *
   * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
   *
   * @param profile The social profile to save.
   * @return The user for whom the profile was saved.
   */
  def saveOrUpdateUser(profile: CustomSocialProfile): Future[User]
}
