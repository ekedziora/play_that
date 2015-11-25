package forms.mappings

import java.time.LocalDate

import play.api.data.FormError
import play.api.data.format.Formatter
import utils.DateTimeUtils.parseLocalDate

class LocalDateMapping extends Formatter[LocalDate] {

  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDate] = {
    data.get(key) map { value =>
      parseLocalDate(value) map (Right(_)) getOrElse Left(Seq(FormError(key, "error.date.format")))
    } getOrElse Left(Seq(FormError(key, "error.required")))
  }

  override def unbind(key: String, value: LocalDate): Map[String, String] = Map(key -> value.toString)
}
