package com.seanshubin.server

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

trait SimplifiedTransformer {
  def fromHttpServletRequest(request: HttpServletRequest): SimplifiedRequest

  def copyToHttpServletResponse(simplifiedResponse: SimplifiedResponse, response: HttpServletResponse)
}
