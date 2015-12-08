package forms

import java.util.UUID

import forms.constraints.CustomFormConstraints
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints
import utils.RegexpUtils

object ChangePasswordForm {

  val form = Form(
    mapping(
      "userId" -> uuid,
      "oldPassword" -> nonEmptyText,
      "newPassword" -> nonEmptyText.verifying(Constraints.pattern(RegexpUtils.PasswordRegexp, error = "password.policy.error")),
      "newPasswordRepeat" -> nonEmptyText
    )(Data.apply)(Data.unapply).verifying(CustomFormConstraints.SamePasswordAndRepeat)
  )

  case class Data(userId: UUID, oldPassword: String, newPassword: String, newPasswordRepeat: String)

}
