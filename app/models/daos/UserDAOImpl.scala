package models.daos

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import forms.AccountDetailsEditForm.Data
import models.User
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

/**
 * Give access to the user object using Slick
 */
class UserDAOImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UserDAO with DAOSlick {

  import driver.api._

  override def findByEmail(email: String): Future[Option[User]] = {
    val query = for {
      dbUser <- slickUsers.filter(_.email === email)
      dbUserLoginInfo <- slickUserLoginInfos.filter(_.userID === dbUser.id)
      dbLoginInfo <- slickLoginInfos.filter(_.id === dbUserLoginInfo.loginInfoId)
    } yield (dbUser, dbLoginInfo)
    db.run(query.result.headOption).map { resultOption =>
      resultOption.map {
        case (user, loginInfo) =>
          User(
            user.userID,
            LoginInfo(loginInfo.providerID, loginInfo.providerKey),
            user.username,
            user.firstName,
            user.lastName,
            user.email,
            user.emailConfirmed,
            user.gender,
            user.birthDate,
            user.avatarURL)
      }
    }
  }

  override def findLoginInfoByUserIdAndProviderId(userId: UUID, providerId: String): Future[LoginInfo] = {
    val query = for {
      dbUserLoginInfo <- slickUserLoginInfos if dbUserLoginInfo.userID === userId
      dbLoginInfo <- slickLoginInfos if dbLoginInfo.id === dbUserLoginInfo.loginInfoId && dbLoginInfo.providerID === providerId
    } yield dbLoginInfo

    db.run(query.result.headOption).map { optionDbLoginInfo =>
      optionDbLoginInfo.map { dbLoginInfo =>
        LoginInfo(dbLoginInfo.providerID, dbLoginInfo.providerKey)
      }.getOrElse {
        throw new IllegalArgumentException(s"No login info found for user $userId and provider $providerId")
      }
    }
  }

  override def updateUserAccount(accountData: Data, userId: UUID): Future[Int] = {
    val q = slickUsers.filter(_.id === userId)
      .map(x => (x.username, x.firstName, x.lastName, x.gender, x.birthDate))
      .update((accountData.username, accountData.firstName, accountData.lastName, accountData.gender, accountData.birthDate))
    db.run(q)
  }

  def find(loginInfo: LoginInfo) = {
    val userQuery = for {
      dbLoginInfo <- loginInfoQuery(loginInfo)
      dbUserLoginInfo <- slickUserLoginInfos.filter(_.loginInfoId === dbLoginInfo.id)
      dbUser <- slickUsers.filter(_.id === dbUserLoginInfo.userID)
    } yield dbUser
    db.run(userQuery.result.headOption).map { dbUserOption =>
      dbUserOption.map { user =>
        User(user.userID, loginInfo, user.username, user.firstName, user.lastName, user.email, user.emailConfirmed, user.gender, user.birthDate, user.avatarURL)
      }
    }
  }

  def find(userID: UUID) = {
    val query = for {
      dbUser <- slickUsers.filter(_.id === userID)
      dbUserLoginInfo <- slickUserLoginInfos.filter(_.userID === dbUser.id)
      dbLoginInfo <- slickLoginInfos.filter(_.id === dbUserLoginInfo.loginInfoId)
    } yield (dbUser, dbLoginInfo)
    db.run(query.result.headOption).map { resultOption =>
      resultOption.map {
        case (user, loginInfo) =>
          User(
            user.userID,
            LoginInfo(loginInfo.providerID, loginInfo.providerKey),
            user.username,
            user.firstName,
            user.lastName,
            user.email,
            user.emailConfirmed,
            user.gender,
            user.birthDate,
            user.avatarURL)
      }
    }
  }

  def findDuplicatedUsername(username: String, exceptUserId: Option[UUID]): Future[Boolean] = {
      val query = slickUsers.filter(_.username === username).filter( users => exceptUserId.map{users.id =!= _}.getOrElse(true: Rep[Boolean]) )
      db.run(query.result.headOption).map { dbUserOption =>
        dbUserOption match {
          case Some(_) => true
          case None => false
        }
      }
  }

  def save(user: User) = {
    val dbUser = new DBUser(user)
    val dbLoginInfo = DBLoginInfo(None, user.loginInfo.providerID, user.loginInfo.providerKey)
    // We don't have the LoginInfo id so we try to get it first.
    // If there is no LoginInfo yet for this user we retrieve the id on insertion.    
    val loginInfoAction = {
      val retrieveLoginInfo = slickLoginInfos.filter(
        info => info.providerID === user.loginInfo.providerID &&
        info.providerKey === user.loginInfo.providerKey).result.headOption
      val insertLoginInfo = slickLoginInfos.returning(slickLoginInfos.map(_.id)).
        into((info, id) => info.copy(id = Some(id))) += dbLoginInfo
      for {
        loginInfoOption <- retrieveLoginInfo
        loginInfo <- loginInfoOption.map(DBIO.successful(_)).getOrElse(insertLoginInfo)
      } yield loginInfo
    }
    // combine database actions to be run sequentially
    val actions = (for {
      _ <- slickUsers.insertOrUpdate(dbUser)
      loginInfo <- loginInfoAction
      _ <- slickUserLoginInfos += DBUserLoginInfo(dbUser.userID, loginInfo.id.get)
    } yield ()).transactionally
    // run actions and return user afterwards
    db.run(actions).map(_ => user)
  }
}
