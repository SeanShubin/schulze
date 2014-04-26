package com.seanshubin.server

class LastHandler(jsonSerialization: JsonSerialization) extends SimplifiedHandler {
  def handle(request: SimplifiedRequest): Option[SimplifiedResponse] = {
    val responseBodyJson = jsonSerialization.toJson(UnhandledRequest("Unsupported Request", request))
    Some(
      SimplifiedResponse(
        HttpResponseCode.BadRequest.code,
        Some(Content(InternetMediaType.Json.name, responseBodyJson)),
        Seq()
      ))
  }
}
