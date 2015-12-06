package forms

import java.time.LocalDateTime
import java.util.UUID

import forms.constraints.CustomFormConstraints.FutureLocalDateTime
import forms.mappings.CustomFormMappings.LocalDateTimeMapping
import play.api.data.Form
import play.api.data.Forms._

object AddEventForm {

  val form = Form(
    mapping(
      "title" -> nonEmptyText,
      "description" -> optional(text),
      "dateTime" -> of(LocalDateTimeMapping).verifying(FutureLocalDateTime),
      "maxParticipants" -> optional(number(min = 1)),
      "ownerId" -> uuid,
      "disciplineId" -> longNumber
    )(Data.apply)(Data.unapply)
  )

  case class Data(title: String, description: Option[String], dateTime: LocalDateTime, maxParticipants: Option[Int], ownerId: UUID, disciplineId: Long)

}
