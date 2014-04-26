package com.seanshubin.server

import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.Server

class JettyServer(port: Int, handler: Handler) extends HttpServer {
  private val server = new Server(port)
  server.setHandler(handler)

  def start() {
    server.start()
  }

  def join() {
    server.join()
  }

  def stop() {
    server.stop()
  }
}
