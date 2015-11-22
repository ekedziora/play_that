package forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints.pattern

/**
 * The form which handles the sign up process.
 */
object SignUpForm {

  /* Minimum 8 characters at least 1 Uppercase Alphabet, 1 Lowercase Alphabet and 1 Number */
  private val PasswordRegexp = """^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$""".r

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
