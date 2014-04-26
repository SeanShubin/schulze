package com.seanshubin.server

trait SimplifiedHandler {
  def handle(request: SimplifiedRequest): Option[SimplifiedResponse]
}
