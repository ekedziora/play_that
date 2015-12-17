package forms.constraints

import java.time.{LocalDate, LocalDateTime}

import forms.ChangePasswordForm
import play.api.data.validation.{Constraint, Invalid, Valid}

object CustomFormConstraints {

  val PastOptionLocalDateConstraint: Constraint[Option[LocalDate]] = Constraint("past.option.local.date")(
    optionLocalDate =>
      optionLocalDate map { localDate =>
          if (localDate.isBefore(LocalDate.now()))
            Valid
          else
            Invalid("error.date.not.past")
      } getOrElse Valid
  )

  val FutureLocalDateTime: Constraint[LocalDateTime] = Constraint("future.local.date.time"){ dateTime =>
    if (dateTime.isAfter(LocalDateTime.now()))
      Valid
    else
      Invalid("error.date.time.not.future")
  }

  val SamePasswordAndRepeat: Constraint[ChangePasswordForm.Data] = Constraint("same.password.repeat")(
    data =>
      if (data.newPassword == data.newPasswordRepeat)
        Valid
      else
        Invalid("password.repeat.not.match")
  )

}
