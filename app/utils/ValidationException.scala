package utils

import play.api.i18n.Messages

class ValidationException private (message: String = null, cause: Throwable = null, validationMessageKey: Option[String] = None, validationMessage: Option[String] = None)
  extends RuntimeException(message, cause) {

  def getMessageForView(implicit messages: Messages): String = {
    if (validationMessageKey.isDefined)
      Messages(validationMessageKey.get)
    else
      validationMessage.getOrElse("")
  }

}

object ValidationException {
  def createWithValidationMessageKey(validationMessageKey: String): ValidationException = {
    new ValidationException(validationMessageKey = Option(validationMessageKey))
  }

  def createWithValidationMessage(validationMessage: String): ValidationException = {
    new ValidationException(validationMessage = Option(validationMessage))
  }
}
