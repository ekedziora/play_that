package utils

object ViewUtils {

  val InfoFlashKey = "info"

  val ErrorFlashKey = "error"

  val InfoStyleClass = "col-md-6 col-md-offset-3 alert alert-info text-center"

  val ErrorStyleClass = "col-md-6 col-md-offset-3 alert alert-danger alert-error text-center"

  val KeyToStyleClass = Map(InfoFlashKey -> InfoStyleClass, ErrorFlashKey -> ErrorStyleClass)
}
