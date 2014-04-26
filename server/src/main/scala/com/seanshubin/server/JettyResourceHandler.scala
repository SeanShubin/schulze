package com.seanshubin.server

import org.eclipse.jetty.server.handler.ResourceHandler

class JettyResourceHandler(resourceBase: String) extends ResourceHandler {
  setDirectoriesListed(true)
  setWelcomeFiles(Array("index.html"))
  setResourceBase(resourceBase)
}
