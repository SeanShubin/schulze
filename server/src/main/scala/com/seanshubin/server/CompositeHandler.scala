package com.seanshubin.server


class CompositeHandler(handlers: Seq[SimplifiedHandler]) extends SimplifiedHandler {
  def handle(request: SimplifiedRequest): Option[SimplifiedResponse] = {
    def handleRequest(handler: SimplifiedHandler): Option[SimplifiedResponse] = handler.handle(request)
    val lazyResponses = handlers.toStream.flatMap(handleRequest)
    if (lazyResponses.isEmpty) None
    else Some(lazyResponses.head)
  }
}
