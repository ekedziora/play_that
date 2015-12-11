package forms

import java.time.LocalDateTime

import forms.constraints.CustomFormConstraints.FutureLocalDateTime
import forms.mappings.CustomFormMappings.LocalDateTimeMapping
import models.Event
import play.api.data.Form
import play.api.data.Forms._

object AddEventForm {

  val form = Form(
    mapping(
      "title" -> nonEmptyText,
      "description" -> optional(text),
      "dateTime" -> of(LocalDateTimeMapping).verifying(FutureLocalDateTime),
      "maxParticipants" -> optional(number(min = 1)),
      "disciplineId" -> longNumber
    )(Data.apply)(Data.unapply)
  )

  case class Data(title: String, description: Option[String], dateTime: LocalDateTime, maxParticipants: Option[Int], disciplineId: Long) {
    def this(event: Event) {
      this(event.title, event.description, event.dateTime, event.maxParticipants, event.disciplineId)
    }
  }

}
