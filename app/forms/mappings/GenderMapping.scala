package forms.mappings

import models.Gender
import play.api.data.FormError
import play.api.data.format.Formatter

class GenderMapping extends Formatter[Gender] {

  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Gender] = {
    data.get(key) map { value =>
      Gender.valueOfOpt(value) map (Right(_)) getOrElse Left(Seq(FormError(key, "error.gender.format")))
    } getOrElse Left(Seq(FormError(key, "error.required")))
  }

  override def unbind(key: String, value: Gender): Map[String, String] = Map(key -> value.name)
}
