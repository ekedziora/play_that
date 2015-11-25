package utils

import java.time.LocalDate

import scala.util.Try

object DateTimeUtils {

  def parseLocalDate(value: String): Option[LocalDate] = {
    Try(LocalDate.parse(value)).toOption
  }

}
