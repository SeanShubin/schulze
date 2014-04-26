package com.seanshubin.server

import scala.collection.mutable.ArrayBuffer

sealed abstract case class HttpResponseCode(name: String, code: Int) {
  HttpResponseCode.valuesBuffer += this

  def matchesString(s: String) = name.equalsIgnoreCase(s)

  def matchesCode(c: Int) = code == c
}

object HttpResponseCode {
  private val valuesBuffer = new ArrayBuffer[HttpResponseCode]
  lazy val values = valuesBuffer.toSeq
  val Continue = new HttpResponseCode("Continue", 100) {}
  val SwitchingProtocols = new HttpResponseCode("SwitchingProtocols", 101) {}
  val Ok = new HttpResponseCode("Ok", 200) {}
  val Created = new HttpResponseCode("Created", 201) {}
  val Accepted = new HttpResponseCode("Accepted", 202) {}
  val NonAuthoritativeInformation = new HttpResponseCode("NonAuthoritativeInformation", 203) {}
  val NoContent = new HttpResponseCode("NoContent", 204) {}
  val ResetContent = new HttpResponseCode("ResetContent", 205) {}
  val PartialContent = new HttpResponseCode("PartialContent", 206) {}
  val MultipleChoices = new HttpResponseCode("MultipleChoices", 300) {}
  val MovedPermanently = new HttpResponseCode("MovedPermanently", 301) {}
  val MovedTemporarily = new HttpResponseCode("MovedTemporarily", 302) {}
  val Found = new HttpResponseCode("Found", 302) {}
  val SeeOther = new HttpResponseCode("SeeOther", 303) {}
  val NotModified = new HttpResponseCode("NotModified", 304) {}
  val UseProxy = new HttpResponseCode("UseProxy", 305) {}
  val TemporaryRedirect = new HttpResponseCode("TemporaryRedirect", 307) {}
  val BadRequest = new HttpResponseCode("BadRequest", 400) {}
  val Unauthorized = new HttpResponseCode("Unauthorized", 401) {}
  val PaymentRequired = new HttpResponseCode("PaymentRequired", 402) {}
  val Forbidden = new HttpResponseCode("Forbidden", 403) {}
  val NotFound = new HttpResponseCode("NotFound", 404) {}
  val MethodNotAllowed = new HttpResponseCode("MethodNotAllowed", 405) {}
  val NotAcceptable = new HttpResponseCode("NotAcceptable", 406) {}
  val ProxyAuthenticationRequired = new HttpResponseCode("ProxyAuthenticationRequired", 407) {}
  val RequestTimeout = new HttpResponseCode("RequestTimeout", 408) {}
  val Conflict = new HttpResponseCode("Conflict", 409) {}
  val Gone = new HttpResponseCode("Gone", 410) {}
  val LengthRequired = new HttpResponseCode("LengthRequired", 411) {}
  val PreconditionFailed = new HttpResponseCode("PreconditionFailed", 412) {}
  val RequestEntityTooLarge = new HttpResponseCode("RequestEntityTooLarge", 413) {}
  val RequestUriTooLong = new HttpResponseCode("RequestUriTooLong", 414) {}
  val UnsupportedMediaType = new HttpResponseCode("UnsupportedMediaType", 415) {}
  val RequestedRangeNotSatisfiable = new HttpResponseCode("RequestedRangeNotSatisfiable", 416) {}
  val ExpectationFailed = new HttpResponseCode("ExpectationFailed", 417) {}
  val InternalServerError = new HttpResponseCode("InternalServerError", 500) {}
  val NotImplemented = new HttpResponseCode("NotImplemented", 501) {}
  val BadGateway = new HttpResponseCode("BadGateway", 502) {}
  val ServiceUnavailable = new HttpResponseCode("ServiceUnavailable", 503) {}
  val GatewayTimeout = new HttpResponseCode("GatewayTimeout", 504) {}
  val HttpVersionNotSupported = new HttpResponseCode("HttpVersionNotSupported", 505) {}

  val enumName: String = getClass.getSimpleName.takeWhile(c => c != '$')

  def fromString(target: String) = {
    val maybeValue = maybeFromString(target)
    maybeValue match {
      case Some(value) => value
      case None => {
        val idealMatchingStringsSeq = values.map(value => value.name)
        val idealMatchingStrings = idealMatchingStringsSeq.mkString(", ")
        throw new RuntimeException(
          s"'$target' does not match a valid $enumName, valid values are $idealMatchingStrings")
      }
    }
  }

  def maybeFromString(target: String) = values.find(value => value.matchesString(target))

  def fromCode(target: Int) = {
    val maybeValue = maybeFromCode(target)
    maybeValue match {
      case Some(value) => value
      case None => {
        val idealMatchingCodesSeq = values.map(value => value.code)
        val idealMatchingCodes = idealMatchingCodesSeq.mkString(", ")
        throw new RuntimeException(
          s"'$target' does not match a valid $enumName, valid values are $idealMatchingCodes")
      }
    }
  }

  def maybeFromCode(target: Int) = values.find(value => value.matchesCode(target))
}
