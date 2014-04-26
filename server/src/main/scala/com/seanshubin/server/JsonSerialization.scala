package com.seanshubin.server

trait JsonSerialization {
  def toJson[T](theObject: T): String

  def fromJson[T](json: String, theClass: Class[T]): T

  def fromJsonArray[T](json: String, theElementClass: Class[T]): Seq[T]
}
