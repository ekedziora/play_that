package models.daos

import java.time.{LocalDate, LocalDateTime}
import java.util.UUID

import com.google.common.base.Joiner
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import models.{Gender, User}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape.proveShapeOf

trait DBTableDefinitions extends CustomTypes {

  protected val driver: JdbcProfile
  import driver.api._

  case class DBUser (
    userID: UUID,
    username: String,
    firstName: Option[String],
    lastName: Option[String],
    email: String,
    emailConfirmed: Boolean,
    gender: Option[Gender],
    birthDate: Option[LocalDate],
    avatarURL: Option[String]
  ) {
    def this(user: User) {
      this(user.userID, user.username, user.firstName, user.lastName, user.email, user.emailConfirmed, user.gender, user.birthDate, user.avatarURL)
    }

    def getFullName: Option[String] = firstName.map { firstNameVal =>
      Joiner.on(" ").join(firstNameVal, lastName.getOrElse(""))
    } orElse lastName
  }

  class Users(tag: Tag) extends Table[DBUser](tag, "user") {
    def id = column[UUID]("userID", O.PrimaryKey)
    def username = column[String]("username")
    def firstName = column[Option[String]]("firstName")
    def lastName = column[Option[String]]("lastName")
    def email = column[String]("email")
    def emailConfirmed = column[Boolean]("emailConfirmed")
    def gender = column[Option[Gender]]("gender")
    def birthDate = column[Option[LocalDate]]("birthDate")
    def avatarURL = column[Option[String]]("avatarURL")
    def uniqueUsername = index("username_unique_index", username, unique = true)
    def uniqueEmail = index("email_unique_index", email, unique = true)
    def * = (id, username, firstName, lastName, email, emailConfirmed, gender, birthDate, avatarURL) <> (DBUser.tupled, DBUser.unapply)
  }

  case class DBUserMailToken(id: UUID, userId: UUID, expirationTime: LocalDateTime, isSignUp: Boolean)

  class UserMailTokens(tag: Tag) extends Table[DBUserMailToken](tag, "user_mail_tokens") {
    def id = column[UUID]("id", O.PrimaryKey)
    def userId = column[UUID]("userId")
    def expirationTime = column[LocalDateTime]("expiration_time")
    def isSignUp = column[Boolean]("is_sign_up")
    def userFk = foreignKey("fk_user_mail_token_user_id", userId, slickUsers)(_.id)
    def * = (id, userId, expirationTime, isSignUp) <> (DBUserMailToken.tupled, DBUserMailToken.unapply)
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
  ) {
    def this(passwordInfo: PasswordInfo, loginInfoId: Long) {
      this(passwordInfo.hasher, passwordInfo.password, passwordInfo.salt, loginInfoId)
    }
  }

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

  case class DbSportDiscipline(id: Long, name: String, nameKey: String)

  class SportDisciplinesTable(tag: Tag) extends Table[DbSportDiscipline](tag, "sport_disciplines") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def nameKey = column[String]("name_key")
    def uniqueName = index("unique_name", name, unique = true)
    def uniqueNameKey = index("unique_name_key", nameKey, unique = true)
    def * = (id, name, nameKey) <> (DbSportDiscipline.tupled, DbSportDiscipline.unapply)
  }

  case class DbEvent(id: Long, title: String, description: Option[String], dateTime: LocalDateTime,
                     maxParticipants: Option[Int], ownerId:UUID, disciplineId: Long, presenceReported: Boolean)

  class EventsTable(tag: Tag) extends Table[DbEvent](tag, "events") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def description = column[Option[String]]("description")
    def dateTime = column[LocalDateTime]("date_time")
    def maxParticipants = column[Option[Int]]("max_participants")
    def ownerId = column[UUID]("owner_id")
    def disciplineId = column[Long]("discipline_id")
    def ownerFk = foreignKey("fk_event_owner", ownerId, slickUsers)(_.id)
    def disciplineFk = foreignKey("fk_event_discipline", disciplineId, sportDisciplinesQuery)(_.id)
    def presenceReported = column[Boolean]("presence_reported")
    def * = (id, title, description, dateTime, maxParticipants, ownerId, disciplineId, presenceReported) <> (DbEvent.tupled, DbEvent.unapply)
    def participants = eventParticipantsQuery.filter(_.eventId === id).flatMap(_.userFk)
    def eventParticipants = eventParticipantsQuery.filter(_.eventId === id)
  }

  case class DbEventParticipant(id: Long, eventId: Long, userId: UUID, present: Option[Boolean])

  class EventParticipants(tag: Tag) extends Table[DbEventParticipant](tag, "event_participants") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def eventId = column[Long]("event_id")
    def userId = column[UUID]("user_id")
    def present = column[Option[Boolean]]("present")
    def eventFk = foreignKey("fk_event_participants_event", eventId, eventsQuery)(_.id)
    def userFk = foreignKey("fk_event_participants_user", userId, slickUsers)(_.id)
    def * = (id, eventId, userId, present) <> (DbEventParticipant.tupled, DbEventParticipant.unapply)
  }

  // table query definitions
  val slickUsers = TableQuery[Users]
  val userMailTokensQuery = TableQuery[UserMailTokens]
  val slickLoginInfos = TableQuery[LoginInfos]
  val slickUserLoginInfos = TableQuery[UserLoginInfos]
  val slickPasswordInfos = TableQuery[PasswordInfos]
  val slickOAuth2Infos = TableQuery[OAuth2Infos]
  val sportDisciplinesQuery = TableQuery[SportDisciplinesTable]
  val eventsQuery = TableQuery[EventsTable]
  val eventParticipantsQuery = TableQuery[EventParticipants]
  
  // queries used in multiple places
  def loginInfoQuery(loginInfo: LoginInfo) = 
    slickLoginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)
}
