package com.seanshubin.server

import scala.collection.mutable.ArrayBuffer

sealed abstract case class InternetMediaType(name: String) {
  InternetMediaType.valuesBuffer += this

  def matchesString(s: String) = name.equalsIgnoreCase(s)
}

object InternetMediaType {
  private val valuesBuffer = new ArrayBuffer[InternetMediaType]
  lazy val values = valuesBuffer.toSeq
  val Text = new InternetMediaType("text/plain") {}
  val Json = new InternetMediaType("application/json") {}

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
