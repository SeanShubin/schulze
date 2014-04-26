package com.seanshubin.schulze.persistence

import collection.mutable.ArrayBuffer

sealed abstract case class ColumnType(name: String) {
  ColumnType.valuesBuffer += this
}

object ColumnType {
  private val valuesBuffer = new ArrayBuffer[ColumnType]
  lazy val values = valuesBuffer.toSeq
  val String = new ColumnType("string") {}
  val Long = new ColumnType("long") {}
  val Reference = new ColumnType("ref") {}
}
