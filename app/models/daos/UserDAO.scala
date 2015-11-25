package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import forms.AccountDetailsEditForm.Data
import models.User

import scala.concurrent.Future

/**
 * Give access to the user object.
 */
trait UserDAO {

  /**
   * Updates user account data
   *
   * @param accountData data to update
   * @return Status of operation
   */
  def updateUserAccount(accountData: Data): Future[Int]

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo): Future[Option[User]]

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: UUID): Future[Option[User]]

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User): Future[User]

  /**
   * Checks if there is user with given username and optionally other id then exceptUserId
   *
   * @param username username to check
   * @param exceptUserId optional id of user to exclude
   * @return true if duplicated user was found, false otherwise
   */
  def findDuplicatedUsername(username: Option[String], exceptUserId: Option[UUID]): Future[Boolean]
}
