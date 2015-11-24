package models.daos

import java.sql.{Date, Timestamp}
import java.time.{LocalDate, LocalDateTime}

import models.Gender
import slick.driver.PostgresDriver.api._

/**
 * Definitions of custom column types
 */
trait CustomTypes {

  implicit def genderDbType = MappedColumnType.base[Gender, String](_.dbValue, Gender.valueOf)

  implicit def localDateType = MappedColumnType.base[LocalDate, Date](Date.valueOf(_), _.toLocalDate)

  implicit def localDateTimeType = MappedColumnType.base[LocalDateTime, Timestamp](Timestamp.valueOf(_), _.toLocalDateTime)

}
