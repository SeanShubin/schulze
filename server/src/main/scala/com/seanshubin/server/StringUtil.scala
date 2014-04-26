package com.seanshubin.server

object StringUtil {
  def escape(target: String) = {
    target.flatMap((ch) => {
      ch match {
        case '\n' => "\\n"
        case '\b' => "\\b"
        case '\t' => "\\t"
        case '\f' => "\\f"
        case '\r' => "\\r"
        case '\"' => "\\\""
        case '\'' => "\\\'"
        case '\\' => "\\\\"
        case x => x.toString
      }
    })
  }

  def doubleQuote(target: String) = "\"" + escape(target) + "\""
}
