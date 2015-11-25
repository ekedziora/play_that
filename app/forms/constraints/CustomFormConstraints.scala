package forms.constraints

import java.time.LocalDate

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

}
