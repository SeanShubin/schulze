package com.seanshubin.schulze.persistence.debug_util

import java.util
import java.util.{ArrayList, HashMap, Collection => JavaCollection, List => JavaList, Map => JavaMap}

import scala.collection.JavaConverters._
import scala.annotation.tailrec
import datomic.{Database, Datom, Peer, Util}

object DatomicReporter {
  def firstObjectOfRow(row: JavaList[AnyRef]): AnyRef = row.get(0)

  def rowsToSequence(rows: JavaCollection[JavaList[AnyRef]]): Seq[JavaList[AnyRef]] = new util.ArrayList(rows).asScala

  def queryAllAttributes(db: Database): Set[String] = {
    val query = datomify(Map(
      ":find" -> Seq("?identity"),
      ":where" -> Seq(
        Seq("_", ":db.install/attribute", "?attribute"),
        Seq("?attribute", ":db/ident", "?identity"))))
    val rows = Peer.q(query, db)
    rowsToSequence(rows).map(firstObjectOfRow).map(_.toString).toSet
  }

  def entityIdsForAttribute(db: Database, attribute: String): Set[Long] = {
    val query = "[:find ?entity :where [?entity " + attribute + "]]"
    val rows = Peer.q(query, db)
    rowsToSequence(rows).map(firstObjectOfRow).map(x => x.asInstanceOf[Long]).toSet
  }

  def queryAllEntityIds(db: Database): Set[Long] = {
    @tailrec
    def loop(soFar: Set[Long], remainingAttributes: List[String]): Set[Long] = {
      if (remainingAttributes.isEmpty) soFar
      else {
        val newIds = entityIdsForAttribute(db, remainingAttributes.head)
        loop(soFar ++ newIds, remainingAttributes.tail)
      }
    }
    val attributes = queryAllAttributes(db)
    val entityIds = loop(Set(), attributes.toList)
    entityIds
  }

  def databaseAndEntityIdToLines(db: Database, id: Long): Seq[String] = {
    val entity = db.entity(id)
    val keys = entity.keySet().asScala.toSeq.sorted
    def keyToLine(key: String) = {
      val value = entity.get(key)
      val line = s"    $key -> $value"
      line
    }
    val idString = "" + id
    val lines = idString +: keys.map(keyToLine)
    lines
  }

  def datomToEntityId(datom: Datom) = datom.e().asInstanceOf[Long]

  def javaListToTupleTwo(row: JavaList[AnyRef]): (Long, AnyRef) = {
    (row.get(0).asInstanceOf[Long], row.get(1))
  }

  def sortTupleTwo(left: (Long, AnyRef), right: (Long, AnyRef)) = left._1 < right._1

  def datomify(root: Any): AnyRef = {
    root match {
      case x: String => Util.read(x)
      case x: Seq[_] => datomify(x)
      case x: Map[_, _] => datomify(x)
      case unsupported => throw new RuntimeException("unsupported type " + unsupported.getClass.getName)
    }
  }

  def datomify(elements: Seq[_]): AnyRef = {
    val javaList: JavaList[AnyRef] = new ArrayList[AnyRef]
    def addToList(element: Any) {
      javaList.add(datomify(element.asInstanceOf[AnyRef]))
    }
    elements.foreach(addToList)
    javaList
  }

  def datomify(keyValues: Map[_, _]): AnyRef = {
    val javaMap: JavaMap[AnyRef, AnyRef] = new HashMap[AnyRef, AnyRef]
    for ((key, value) <- keyValues) {
      javaMap.put(datomify(key), datomify(value))
    }
    javaMap
  }


  def databaseAndAttributeToLines(db: Database, attribute: String): Seq[String] = {
    def formatAttributeRow(row: (Long, AnyRef)): String = {
      val (entityId, value) = row
      s"    $entityId $value"
    }

    val query = datomify(Map(
      ":find" -> Seq("?entity", "?value"),
      ":where" -> Seq(Seq("?entity", attribute, "?value"))))
    val rows = Peer.q(query, db)
    val rowLines = new util.ArrayList(rows).asScala.map(javaListToTupleTwo).sortWith(sortTupleTwo).map(formatAttributeRow)
    val lines = attribute +: rowLines
    lines
  }

  def report(db: Database): Seq[String] = {
    val allAttributes = queryAllAttributes(db).toSeq.sorted
    def attributeToLines(attribute: String) = databaseAndAttributeToLines(db, attribute)
    val byColumnLines = allAttributes.flatMap(attributeToLines)

    val datoms = db.datoms(Database.EAVT).asScala
    def entityIdToLines(entityId: Long): Seq[String] = databaseAndEntityIdToLines(db, entityId)
    val byRowLines = datoms.map(datomToEntityId).toSet.toSeq.sorted.flatMap(entityIdToLines)

    val lines = byRowLines ++ byColumnLines
    lines
  }

  def report(baseline: Database, db: Database): Seq[String] = {
    val ignoreAttributes = queryAllAttributes(baseline)
    val allAttributes = queryAllAttributes(db)
    val relevantAttributes = (allAttributes -- ignoreAttributes).toSeq.sorted
    def attributeToLines(attribute: String) = databaseAndAttributeToLines(db, attribute)
    val byColumnLines = relevantAttributes.flatMap(attributeToLines)

    val ignoreEntityIds = baseline.datoms(Database.EAVT).asScala.map(datomToEntityId).toSet
    val allEntityIds = db.datoms(Database.EAVT).asScala.map(datomToEntityId).toSet
    val relevantEntityIds = (allEntityIds -- ignoreEntityIds).toSeq.sorted
    def entityIdToLines(entityId: Long): Seq[String] = databaseAndEntityIdToLines(db, entityId)
    val byRowLines = relevantEntityIds.flatMap(entityIdToLines)

    val lines = byRowLines ++ byColumnLines
    lines
  }
}
