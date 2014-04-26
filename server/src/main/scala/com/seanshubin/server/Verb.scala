package com.seanshubin.server

import java.net.URLDecoder
import scala.collection.mutable.ArrayBuffer

sealed abstract case class Verb(name: String) {
  Verb.valuesBuffer += this

  def matchesString(s: String) = name.equalsIgnoreCase(s)

  def unapplySeq(request: SimplifiedRequest): Option[Seq[String]] =
    if (matchesString(request.method))
      Some(request.path.split("/", -1).map(URLDecoder.decode(_, "UTF-8")).toList.tail)
    else None
}

object Verb {
  private val valuesBuffer = new ArrayBuffer[Verb]
  lazy val values = valuesBuffer.toSeq

  val Post = new Verb("POST") {}
  val Get = new Verb("GET") {}
  val Put = new Verb("PUT") {}
  val Patch = new Verb("PATCH") {}
  val Delete = new Verb("DELETE") {}
  val Options = new Verb("OPTIONS") {}

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
}
