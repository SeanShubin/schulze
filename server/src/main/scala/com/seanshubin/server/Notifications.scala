package com.seanshubin.server


trait Notifications {
  def datomicReady()

  def gotRequest(request: SimplifiedRequest)

  def aboutToSendResponse(request: SimplifiedRequest, response: SimplifiedResponse)
}
