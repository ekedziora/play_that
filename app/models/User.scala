package models

import java.time.LocalDate
import java.util.UUID

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}

case class User(
  userID: UUID,
  loginInfo: LoginInfo,
  username: Option[String] = None,
  firstName: Option[String] = None,
  lastName: Option[String] = None,
  email: Option[String] = None,
  emailConfirmed: Boolean = false,
  gender: Option[Gender] = None,
  birthDate: Option[LocalDate] = None,
  avatarURL: Option[String] = None) extends Identity
