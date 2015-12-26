import play.api.GlobalSettings
import play.api.mvc.{RequestHeader, Result}
import utils.ControllerUtils

import scala.concurrent.Future

object Global extends GlobalSettings {
  override def onHandlerNotFound(request: RequestHeader): Future[Result] = {
    Future.successful(ControllerUtils.getDefaultNotFoundResponse(request))
  }
}