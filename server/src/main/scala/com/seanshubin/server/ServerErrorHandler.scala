package com.seanshubin.server

trait ServerErrorHandler {
  def handle(request: SimplifiedRequest, exception: ExceptionInfo): SimplifiedResponse
}
