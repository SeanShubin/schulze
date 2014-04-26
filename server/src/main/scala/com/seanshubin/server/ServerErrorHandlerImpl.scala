package com.seanshubin.server

class ServerErrorHandlerImpl(jsonSerialization: JsonSerialization) extends ServerErrorHandler {
  def handle(request: SimplifiedRequest, exception: ExceptionInfo): SimplifiedResponse = {
    val responseObject = ServerErrorDuringRequest(request, exception)
    val responseBodyJson = jsonSerialization.toJson(responseObject)
    SimplifiedResponse(
      HttpResponseCode.InternalServerError.code,
      Some(Content(InternetMediaType.Json.name, responseBodyJson))
    )
  }
}
