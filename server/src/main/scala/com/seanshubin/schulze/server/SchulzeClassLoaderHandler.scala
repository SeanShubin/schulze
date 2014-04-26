package com.seanshubin.schulze.server

import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.Request
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import java.io.{OutputStream, InputStream}
import scala.annotation.tailrec

class SchulzeClassLoaderHandler extends AbstractHandler {

  def handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
    val uri = request.getRequestURI
    if (weCanHandle(uri)) {
      val inputStream = classOf[SchulzeClassLoaderAnchor].getResourceAsStream(uri)
      if (inputStream == null) {
        baseRequest.setHandled(false)
      } else {
        try {
          val outputStream = response.getOutputStream
          feedInputStreamToOutputStream(inputStream, outputStream)
          outputStream.flush()
          baseRequest.setHandled(true)
        } finally {
          inputStream.close()
        }
      }
    } else {
      baseRequest.setHandled(false)
    }
  }

  private def weCanHandle(uri: String): Boolean = {
    (uri.startsWith("/lib/") || uri.startsWith("/schulze/") || uri == "/require-config.js") && (uri.endsWith(".js") || uri.endsWith(".css") || uri.endsWith(".html") || uri.endsWith(".txt"))
  }

  private def feedInputStreamToOutputStream(inputStream: InputStream, outputStream: OutputStream) {
    @tailrec
    def loop(byte: Int) {
      if (byte != -1) {
        outputStream.write(byte)
        loop(inputStream.read())
      }
    }
    loop(inputStream.read())
  }
}
