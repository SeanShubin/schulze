package com.seanshubin.server

case class TestObject(id: Int, name: String, data: Seq[String])

case class TestObjectWithoutId(name: String, data: Seq[String])

class TestHandler(jsonSerialization: JsonSerialization) extends SimplifiedHandler {
  private var testObjects: Seq[TestObject] = Seq()
  private var byId: Map[Int, TestObject] = Map()
  private var nextId: Int = 1

  def handle(request: SimplifiedRequest): Option[SimplifiedResponse] = {
    import Verb._
    val maybeResponse = request match {
      case Get("test", idString) => get(request, idString)
      case Get("test") => get(request)
      case Put("test", idString) => put(request, idString)
      case Post("test") => post(request)
      case Delete("test", idString) => delete(request, idString)
      case _ => None
    }
    maybeResponse
  }

  private def get(request: SimplifiedRequest): Option[SimplifiedResponse] = {
    val body = jsonSerialization.toJson(testObjects)
    okJsonResponse(body)
  }

  private def get(request: SimplifiedRequest, idString: String): Option[SimplifiedResponse] = {
    val id = idString.toInt
    val testObject = byId(id)
    val body = jsonSerialization.toJson(testObject)
    okJsonResponse(body)
  }

  private def post(request: SimplifiedRequest): Option[SimplifiedResponse] = {
    val testObjectWithoutId = jsonSerialization.fromJson(request.body, classOf[TestObjectWithoutId])
    val testObject = TestObject(nextId, testObjectWithoutId.name, testObjectWithoutId.data)
    nextId += 1
    testObjects = testObjects :+ testObject
    byId += (testObject.id -> testObject)
    val body = jsonSerialization.toJson(testObject.id)
    okJsonResponse(body)
  }

  private def put(request: SimplifiedRequest, idString: String): Option[SimplifiedResponse] = {
    val testObjectWithoutId = jsonSerialization.fromJson(request.body, classOf[TestObjectWithoutId])
    val id = idString.toInt
    val testObject = TestObject(id, testObjectWithoutId.name, testObjectWithoutId.data)
    byId += (testObject.id -> testObject)
    emptyOkResponse
  }

  private def delete(request: SimplifiedRequest, idString: String): Option[SimplifiedResponse] = {
    val id = idString.toInt
    testObjects = testObjects.filterNot(_.id == id)
    byId = byId - id
    emptyOkResponse
  }

  private def okJsonResponse(body: String): Option[SimplifiedResponse] = {
    val code = HttpResponseCode.Ok.code
    val mediaType = InternetMediaType.Json.name
    val content = Content(mediaType, body)
    Some(SimplifiedResponse(code, Some(content), headers))
  }

  private val headers: Seq[(String, String)] = Seq(
    ("Content-Type", InternetMediaType.Json.name)
  )
  private val emptyOkResponse = Some(SimplifiedResponse(HttpResponseCode.Ok.code, None, headers))
}
