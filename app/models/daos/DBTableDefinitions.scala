package models.daos

import java.time.LocalDate
import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.{Gender, User}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape.proveShapeOf

trait DBTableDefinitions extends CustomTypes {

  protected val driver: JdbcProfile
  import driver.api._

  case class DBUser (
    userID: UUID,
    username: Option[String],
    firstName: Option[String],
    lastName: Option[String],
    email: Option[String],
    gender: Option[Gender],
    birthDate: Option[LocalDate],
    avatarURL: Option[String]
  ) {
    def this(user: User) {
      this(user.userID, user.username, user.firstName, user.lastName, user.email, user.gender, user.birthDate, user.avatarURL)
    }
  }

  class Users(tag: Tag) extends Table[DBUser](tag, "user") {
    def id = column[UUID]("userID", O.PrimaryKey)
    def username = column[Option[String]]("username")
    def firstName = column[Option[String]]("firstName")
    def lastName = column[Option[String]]("lastName")
    def email = column[Option[String]]("email")
    def gender = column[Option[Gender]]("gender")
    def birthDate = column[Option[LocalDate]]("birthDate")
    def avatarURL = column[Option[String]]("avatarURL")
    def uniqueUsername = index("username_unique_index", username, unique = true)
    def uniqueEmail = index("email_unique_index", email, unique = true)
    def * = (id, username, firstName, lastName, email, gender, birthDate, avatarURL) <> (DBUser.tupled, DBUser.unapply)
  }

  case class DBLoginInfo (
    id: Option[Long],
    providerID: String,
    providerKey: String
  )

  class LoginInfos(tag: Tag) extends Table[DBLoginInfo](tag, "logininfo") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def providerID = column[String]("providerID")
    def providerKey = column[String]("providerKey")
    def * = (id.?, providerID, providerKey) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
  }

  case class DBUserLoginInfo (
    userID: UUID,
    loginInfoId: Long
  )

  class UserLoginInfos(tag: Tag) extends Table[DBUserLoginInfo](tag, "userlogininfo") {
    def userID = column[UUID]("userID")
    def loginInfoId = column[Long]("loginInfoId")
    def loginInfoFk = foreignKey("fk_user_login_info_login_info", loginInfoId, slickLoginInfos)(_.id)
    def userFk = foreignKey("fk_user_login_info_user_id", userID, slickUsers)(_.id)
    def * = (userID, loginInfoId) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
  }

  case class DBPasswordInfo (
    hasher: String,
    password: String,
    salt: Option[String],
    loginInfoId: Long
  )

  class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, "passwordinfo") {
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")
    def loginInfoId = column[Long]("loginInfoId")
    def loginInfoFk = foreignKey("fk_password_info_login_info", loginInfoId, slickLoginInfos)(_.id)
    def * = (hasher, password, salt, loginInfoId) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }

  case class DBOAuth2Info (
    id: Option[Long],
    accessToken: String,
    tokenType: Option[String],
    expiresIn: Option[Int],
    refreshToken: Option[String],
    loginInfoId: Long
  )

  class OAuth2Infos(tag: Tag) extends Table[DBOAuth2Info](tag, "oauth2info") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def accessToken = column[String]("accesstoken")
    def tokenType = column[Option[String]]("tokentype")
    def expiresIn = column[Option[Int]]("expiresin")
    def refreshToken = column[Option[String]]("refreshtoken")
    def loginInfoId = column[Long]("loginInfoId")
    def loginInfoFk = foreignKey("fk_oauth2_info_login_info", loginInfoId, slickLoginInfos)(_.id)
    def * = (id.?, accessToken, tokenType, expiresIn, refreshToken, loginInfoId) <> (DBOAuth2Info.tupled, DBOAuth2Info.unapply)
  }

  // table query definitions
  val slickUsers = TableQuery[Users]
  val slickLoginInfos = TableQuery[LoginInfos]
  val slickUserLoginInfos = TableQuery[UserLoginInfos]
  val slickPasswordInfos = TableQuery[PasswordInfos]
  val slickOAuth2Infos = TableQuery[OAuth2Infos]
  
  // queries used in multiple places
  def loginInfoQuery(loginInfo: LoginInfo) = 
    slickLoginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)
}
