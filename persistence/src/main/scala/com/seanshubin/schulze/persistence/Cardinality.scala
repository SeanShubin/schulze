package com.seanshubin.schulze.persistence

import collection.mutable.ArrayBuffer

sealed abstract case class Cardinality(name: String) {
  Cardinality.valuesBuffer += this
}

object Cardinality {
  private val valuesBuffer = new ArrayBuffer[Cardinality]
  lazy val values = valuesBuffer.toSeq
  val One = new Cardinality("one") {}
  val Many = new Cardinality("many") {}
}
