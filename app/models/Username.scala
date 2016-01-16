package models

/**
  * Helper model class for representing user name
  *
  * @param fullName option user full name
  * @param username user username
  */
case class Username(fullName: Option[String], username: String) {
  val getFullNameThenUsername = fullName.getOrElse(username)
}

object Username {
  def empty = Username(None, "")
}