package com.seanshubin.server

import scala.annotation.tailrec


case class SimplifiedRequest(path: String, method: String, headers: Seq[(String, String)], parameters: Seq[(String, Seq[String])], body: String) {
  def toMultipleLineString: Seq[String] = {
    val multipleLineString = Seq(s"path = $path") ++
      Seq(s"method = $method") ++
      headersAsMultipleLineString ++
      parametersAsMultipleLineString ++
      bodyAsMultipleLineString
    multipleLineString
  }

  def parametersAsMapOfFirstOccurrence(): Map[String, String] = {
    @tailrec
    def loop(soFar: Map[String, String], remainingParameters: List[(String, Seq[String])]): Map[String, String] = {
      if (remainingParameters.isEmpty) soFar
      else {
        val (key, values) = remainingParameters.head
        if (soFar.contains(key)) loop(soFar, remainingParameters.tail)
        else loop(soFar + (key -> values.head), remainingParameters.tail)
      }
    }
    loop(Map(), parameters.toList)
  }

  private def headersAsMultipleLineString: Seq[String] = {
    val headerLines: Seq[String] = for ((name, value) <- headers) yield {
      val nameString = StringUtil.escape(name)
      val valueString = StringUtil.doubleQuote(value)
      s"$nameString = $valueString"
    }
    val headersCount = headers.size
    val caption = Seq(s"headers ($headersCount):")
    val lines = caption ++ headerLines.map(indent)
    lines
  }

  private def parametersAsMultipleLineString: Seq[String] = {
    val lines: Seq[String] = for {
      (name, values) <- parameters
      value <- values
    } yield {
      val nameString = StringUtil.escape(name)
      val valueString = StringUtil.doubleQuote(value)
      s"$nameString = $valueString"
    }
    val parametersCount = parameters.size
    val caption = Seq(s"parameters ($parametersCount):")
    caption ++ lines.map(indent)
  }

  private def bodyAsMultipleLineString: Seq[String] = {
    val bodyLines: Seq[String] = if (body.size == 0) Seq() else body.split("\r\n|\r|\n")
    val bodyLineCount = bodyLines.size
    val linesWord = if (bodyLineCount == 1) "line" else "lines"
    val caption = if (bodyLineCount == 0) "body is empty" else s"body ($bodyLineCount $linesWord):"
    val lines: Seq[String] = bodyLines.map(StringUtil.doubleQuote)
    Seq(caption) ++ lines.map(indent)
  }

  private def indent(target: String) = s"    $target"
}
