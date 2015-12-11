package forms.mappings

import java.time.LocalDateTime

import play.api.data.FormError
import play.api.data.format.Formatter
import utils.DateTimeUtils.{formatLocalDateTime, parseLocalDateTime}

class LocalDateTimeMapping extends Formatter[LocalDateTime] {

  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDateTime] = {
    data.get(key) map { value =>
      parseLocalDateTime(value) map (Right(_)) getOrElse Left(Seq(FormError(key, "error.date.time.format")))
    } getOrElse Left(Seq(FormError(key, "error.required")))
  }

  override def unbind(key: String, value: LocalDateTime): Map[String, String] = Map(key -> formatLocalDateTime(value))
}
