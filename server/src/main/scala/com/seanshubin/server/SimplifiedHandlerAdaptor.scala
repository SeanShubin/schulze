package com.seanshubin.server

import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.Request
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

class SimplifiedHandlerAdaptor(simplifiedTransformer: SimplifiedTransformer,
                               simplifiedHandler: SimplifiedHandler,
                               notifications: Notifications,
                               serverErrorHandler: ServerErrorHandler) extends AbstractHandler {
  def handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
    val simplifiedRequest = simplifiedTransformer.fromHttpServletRequest(request)
    notifications.gotRequest(simplifiedRequest)
    val maybeSimplifiedResponse = try {
      simplifiedHandler.handle(simplifiedRequest)
    } catch {
      case exception: Throwable =>
        val exceptionInfo = ExceptionInfo.fromException(exception)
        Some(serverErrorHandler.handle(simplifiedRequest, exceptionInfo))
    }
    maybeSimplifiedResponse match {
      case Some(simplifiedResponse) =>
        notifications.aboutToSendResponse(simplifiedRequest, simplifiedResponse)
        simplifiedTransformer.copyToHttpServletResponse(simplifiedResponse, response)
        baseRequest.setHandled(true)
      case None =>
        baseRequest.setHandled(false)
    }
  }
}
