package forms.constraints

import java.time.LocalDate

import forms.ChangePasswordForm
import play.api.data.validation.{Constraint, Invalid, Valid}

object CustomFormConstraints {

  val PastOptionLocalDateConstraint: Constraint[Option[LocalDate]] = Constraint("past.option.local.date")(
    optionLocalDate =>
      optionLocalDate map { localDate =>
          if (localDate.isAfter(LocalDate.now()))
            Invalid("error.date.not.past")
          else
            Valid
      } getOrElse Valid
  )

  val SamePasswordAndRepeat: Constraint[ChangePasswordForm.Data] = Constraint("same.password.repeat")(
    data =>
      if (data.newPassword == data.newPasswordRepeat)
        Valid
      else
        Invalid("password.repeat.not.match")
  )

}
