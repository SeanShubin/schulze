package com.seanshubin.schulze.server

import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.Request
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

class WelcomePageHandler extends AbstractHandler {
  def handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
    if (target == "/") {
      response.sendRedirect("/schulze/elections/elections.html")
      baseRequest.setHandled(true)
    } else {
      baseRequest.setHandled(false)
    }
  }
}
