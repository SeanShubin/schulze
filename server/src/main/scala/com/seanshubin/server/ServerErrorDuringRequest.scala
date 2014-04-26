package com.seanshubin.server

case class ServerErrorDuringRequest(request: SimplifiedRequest, exception: ExceptionInfo)
