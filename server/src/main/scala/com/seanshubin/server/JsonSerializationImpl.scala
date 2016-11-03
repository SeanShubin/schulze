package com.seanshubin.server

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import java.io.StringWriter
import com.fasterxml.jackson.annotation.JsonInclude
import scala.collection.JavaConverters._

class JsonSerializationImpl extends JsonSerialization {
  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)

  def toJson[T](theObject: T): String = {
    val stringWriter = new StringWriter
    mapper.writeValue(stringWriter, theObject)
    val json = stringWriter.toString
    json
  }

  def fromJson[T](json: String, theClass: Class[T]): T = {
    val parsed = mapper.readValue(json, theClass)
    parsed
  }

  def fromJsonArray[T](json: String, theElementClass: Class[T]): Seq[T] = {
    val collectionType = mapper.getTypeFactory.constructCollectionType(classOf[java.util.List[T]], theElementClass)
    val myObjects: java.util.List[T] = mapper.readValue(json, collectionType)
    myObjects.asScala
  }
}

case class Sample(rank: Option[Int], candidate: String)

object JsonSerializationImpl extends App {
  val json = "[{\"rank\":1,\"candidate\":\"Vanilla\"},{\"rank\":2,\"candidate\":\"Chocolate\"},{\"rank\":3,\"candidate\":\"Strawberry\"}]"
  val jsonSerialization = new JsonSerializationImpl
  val samples: Seq[Sample] = jsonSerialization.fromJsonArray(json, classOf[Sample])
  println(samples.length)
  println(samples(0).rank)
  println(samples(0).candidate)
}
