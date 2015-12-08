package forms

import java.time.LocalDate

import forms.constraints.CustomFormConstraints.PastOptionLocalDateConstraint
import forms.mappings.CustomFormMappings._
import models.{Gender, User}
import play.api.data.Form
import play.api.data.Forms._

object AccountDetailsEditForm {

  val form = Form(
    mapping(
      "username" -> nonEmptyText,
      "firstName" -> optional(text),
      "lastName" -> optional(text),
      "gender" -> optional(of(GenderMapping)),
      "birthDate" -> optional(of(LocalDateMapping)).verifying(PastOptionLocalDateConstraint)
    )(Data.apply)(Data.unapply)
  )

  case class Data(username: String, firstName: Option[String], lastName: Option[String], gender: Option[Gender], birthDate: Option[LocalDate]) {
    def this(user: User) {
      this(user.username.getOrElse(""), user.firstName, user.lastName, user.gender, user.birthDate)
    }
  }

}
