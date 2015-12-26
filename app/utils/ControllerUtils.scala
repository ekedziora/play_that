package utils

import play.api.mvc.{RequestHeader, Results}

object ControllerUtils {

  def getDefaultNotFoundResponse(implicit request: RequestHeader) = {
    Results.NotFound(views.html.defaultpages.notFound(request.method, request.uri))
  }

}
