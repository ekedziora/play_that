package models

import utils.EnumOf

sealed class Gender(val dbValue: String, val messagesKey: String)

object Gender extends EnumOf[Gender] {
  case object Male extends Gender("M", "gender.label.male")
  case object Female extends Gender("F", "gender.label.female")
}
