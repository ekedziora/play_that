package utils

class NotFoundException private (message: String = null, cause: Throwable = null, descriptionKey: Option[String] = None) extends RuntimeException(message, cause) {

}

object NotFoundException {
  def apply(descriptionKey: String) = new NotFoundException(descriptionKey = Option(descriptionKey))
  def apply() = new NotFoundException()
}
