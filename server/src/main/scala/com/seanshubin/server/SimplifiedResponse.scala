package com.seanshubin.server

case class SimplifiedResponse(code: Int, maybeContent: Option[Content] = None, headers: Seq[(String, String)] = Seq())
