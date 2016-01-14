package forms

import java.time.LocalDateTime

import forms.mappings.CustomFormMappings.LocalDateTimeMapping
import play.api.data.Form
import play.api.data.Forms._

object ListFiltersForm {

  val form = Form(
    mapping(
      "title" -> optional(text),
      "dateTimeFrom" -> optional(of(LocalDateTimeMapping)),
      "dateTimeTo" -> optional(of(LocalDateTimeMapping)),
      "disciplineId" -> optional(longNumber),
      "spotsAvailable" -> optional(boolean)
    )(Data.apply)(Data.unapply)
  )

  case class Data(title: Option[String], dateTimeFrom: Option[LocalDateTime], dateTimeTo: Option[LocalDateTime],
                  disciplineId: Option[Long], spotsAvailable: Option[Boolean])

}
