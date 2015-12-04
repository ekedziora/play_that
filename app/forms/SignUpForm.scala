package forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints.pattern
import utils.RegexpUtils.PasswordRegexp

/**
 * The form which handles the sign up process.
 */
object SignUpForm {

  val form = Form(
    mapping(
      "username" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText.verifying(pattern(PasswordRegexp, error = "password.policy.error"))
    )(Data.apply)(Data.unapply)
  )

  case class Data(
    username: String,
    email: String,
    password: String)
}
