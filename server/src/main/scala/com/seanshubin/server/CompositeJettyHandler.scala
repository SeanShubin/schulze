package com.seanshubin.server

import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.handler.HandlerList

class CompositeJettyHandler(handlers: Seq[Handler]) extends HandlerList {
  setHandlers(handlers.toArray)
}
