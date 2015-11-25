package models

import utils.EnumOf

sealed class Gender(val name: String, val dbValue: String, val messagesKey: String)

object Gender extends EnumOf[Gender] {
  case object Male extends Gender("Male", "M", "gender.label.male")
  case object Female extends Gender("Female" ,"F", "gender.label.female")

  def getOptions: Seq[(String, String)] = {
    Gender.values map (gender => (gender.name, gender.messagesKey))
  }

  def fromDbValue(dbValue: String) = {
    Gender.values.find(_.dbValue == dbValue) getOrElse {
      throw new IllegalArgumentException(s"No gender value for database value $dbValue")
    }
  }
}
