package forms

import forms.constraints.CustomFormConstraints
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints
import utils.RegexpUtils

object ChangePasswordForm {

  val form = Form(
    mapping(
      "oldPassword" -> nonEmptyText,
      "newPassword" -> nonEmptyText.verifying(Constraints.pattern(RegexpUtils.PasswordRegexp, error = "password.policy.error")),
      "newPasswordRepeat" -> nonEmptyText
    )(Data.apply)(Data.unapply).verifying(CustomFormConstraints.SamePasswordAndRepeat)
  )

  case class Data(oldPassword: String, newPassword: String, newPasswordRepeat: String)

}
