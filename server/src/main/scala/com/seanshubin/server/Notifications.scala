package com.seanshubin.server

trait Notifications {
  def configuration(configuration: Configuration)

  def datomicReady()

  def gotRequest(request: SimplifiedRequest)

  def aboutToSendResponse(request: SimplifiedRequest, response: SimplifiedResponse)
}
