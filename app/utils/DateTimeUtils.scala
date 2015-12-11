package utils

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

import scala.util.Try

object DateTimeUtils {

  private val dateTimeFormatter: DateTimeFormatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

  def parseLocalDate(value: String): Option[LocalDate] = {
    Try(LocalDate.parse(value)).toOption
  }

  def parseLocalDateTime(value: String): Option[LocalDateTime] = {
    Try(LocalDateTime.parse(value, dateTimeFormatter)).toOption
  }

  def formatLocalDateTime(localDateTime: LocalDateTime): String = {
    localDateTime.format(dateTimeFormatter)
  }

}
