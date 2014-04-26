package com.seanshubin.server

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import scala.collection.JavaConversions._
import java.io.{OutputStream, ByteArrayOutputStream, InputStream}
import java.nio.charset.Charset
import scala.annotation.tailrec

class SimplifiedTransformerImpl(charset: Charset) extends SimplifiedTransformer {
  def fromHttpServletRequest(request: HttpServletRequest): SimplifiedRequest = {
    val path: String = request.getRequestURI
    val method: String = request.getMethod
    val headerNames: Seq[String] = enumerationAsScalaIterator(request.getHeaderNames).toIndexedSeq
    def headerFromName(headerName: String): (String, String) = (headerName, request.getHeader(headerName))
    val headers: Seq[(String, String)] = headerNames.map(headerFromName)
    val parameterNames: Seq[String] = enumerationAsScalaIterator(request.getParameterNames).toIndexedSeq
    def parameterFromName(parameterName: String): (String, Seq[String]) = (parameterName, request.getParameterValues(parameterName))
    val parameters: Seq[(String, Seq[String])] = parameterNames.map(parameterFromName)
    val body: String = loadInputStreamIntoString(request.getInputStream, charset)
    val simplifiedRequest = SimplifiedRequest(path, method, headers, parameters, body)
    simplifiedRequest
  }


  def copyToHttpServletResponse(simplifiedResponse: SimplifiedResponse, response: HttpServletResponse) {
    response.setStatus(simplifiedResponse.code)
    def setHeader(header: (String, String)) {
      val (name, value) = header
      response.setHeader(name, value)
    }
    simplifiedResponse.headers.foreach(setHeader)
    simplifiedResponse.maybeContent match {
      case Some(Content(internetMediaType, body)) =>
        response.setContentType(internetMediaType)
        response.setCharacterEncoding(charset.name)
        val writer = response.getWriter
        writer.print(body)
        writer.flush()
      case None =>
    }
  }

  private def loadInputStreamIntoString(inputStream: InputStream, charset: Charset): String = {
    val outputStream = new ByteArrayOutputStream()
    @tailrec
    def writeRemainingBytes(inputStream: InputStream, outputStream: OutputStream) {
      val nextCharacter = inputStream.read()
      if (nextCharacter == -1) return
      else {
        outputStream.write(nextCharacter)
        writeRemainingBytes(inputStream, outputStream)
      }
    }
    writeRemainingBytes(inputStream, outputStream)
    val body = outputStream.toString(charset.name())
    body
  }
}
