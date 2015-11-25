package models.services

import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import forms.AccountDetailsEditForm
import models.User

import scala.concurrent.Future

/**
 * Handles actions to users.
 */
trait UserService extends IdentityService[User] {

  /**
   * Updates user account data
   *
   * @param accountData user account data to update
   * @return status of operation
   */
  def updateAccountDetails(accountData: AccountDetailsEditForm.Data) : Future[Int]

  /**
   * Checks if there exists user with given username
   *
   * @param username username to check
   * @return true if user with username exists, false otherwise
   */
  def findDuplicatedUsername(username: Option[String]): Future[Boolean]

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User): Future[User]

  /**
   * Saves the social profile for a user.
   *
   * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
   *
   * @param profile The social profile to save.
   * @return The user for whom the profile was saved.
   */
  def save(profile: CommonSocialProfile): Future[User]
}
