package models

import java.time.LocalDate
import java.util.UUID

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}

case class User(
  userID: UUID,
  loginInfo: LoginInfo,
  username: String,
  firstName: Option[String] = None,
  lastName: Option[String] = None,
  email: String,
  emailConfirmed: Boolean = false,
  gender: Option[Gender] = None,
  birthDate: Option[LocalDate] = None,
  avatarURL: Option[String] = None) extends Identity
