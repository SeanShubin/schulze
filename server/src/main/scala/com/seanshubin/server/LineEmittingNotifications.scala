package com.seanshubin.server


class LineEmittingNotifications(emit: String => Unit) extends Notifications {

  override def configuration(configuration: Configuration): Unit = {
    emit(s"port       = ${configuration.port}")
    emit(s"datomicUri = '${configuration.datomicUri}'")
    emit(s"Waiting on datomic...")
  }

  def datomicReady() {
    emit("Datomic Ready")
  }

  def gotRequest(request: SimplifiedRequest) {
    //    if(isFavoriteIcon(request)) return
    //    emit(requestLine(request))
  }

  def aboutToSendResponse(request: SimplifiedRequest, response: SimplifiedResponse) {
    if (isFavoriteIcon(request)) return
    emit(requestLine(request))
    emit(responseLine(response))
  }

  private def requestLine(request: SimplifiedRequest) = {
    val SimplifiedRequest(path, method, headers, parameters, body) = request
    val line = f"$method%-7s $path%s $body%s"
    line
  }

  private def responseLine(response: SimplifiedResponse) = {
    response match {
      case SimplifiedResponse(code, Some(Content(internetMediaType, body)), _) =>
        s"  $code $body"
      case SimplifiedResponse(code, None, _) =>
        s"  $code"
    }
  }


  private def isFavoriteIcon(request: SimplifiedRequest): Boolean = {
    request.method == "GET" && request.path == "/favicon.ico"
  }
}
