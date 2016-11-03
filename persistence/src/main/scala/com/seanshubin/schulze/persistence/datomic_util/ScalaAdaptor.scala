package com.seanshubin.schulze.persistence.datomic_util

import java.util
import java.util.{Map => JavaMap}
import java.util.{List => JavaList}

import datomic.{Connection, Entity, Peer}

import scala.collection.JavaConverters._

object ScalaAdaptor {
  def transact(connection: Connection, transactions: Seq[Any]) = {
    val datomifiedTransactions = datomifySeq(transactions)
    val result = connection.transact(datomifiedTransactions)
    result.get()
    result
  }

  def queryRows[T](convertRow: JavaList[AnyRef] => T, query: Any, parameters: Any*): Seq[T] = {
    val javaRows = Peer.q(datomify(query), parameters.map(datomify): _*)
    val scalaRows = javaRows.asScala.map(convertRow).toSeq
    scalaRows
  }

  def countRows(query: Any, parameters: Any*): Long = {
    val javaRows = Peer.q(datomify(query), parameters.map(datomify): _*)
    javaRows.size()
  }

  def entityToMap(entity:Entity):Map[String, AnyRef] = {
    val keys = entity.keySet().asScala
    val entries = for {
      key <- keys
    } yield {
      (key, entity.get(key))
    }
    val map = entries.toMap
    map
  }

  def querySingleRow[T](convertRow: JavaList[AnyRef] => T, query: Any, parameters: Any*): T = {
    val javaRows = Peer.q(datomify(query), parameters.map(datomify): _*)
    if (javaRows.size() != 1) throw new RuntimeException("expected only a single row, got " + javaRows.size)
    val scalaRow = convertRow(javaRows.iterator().next())
    scalaRow
  }

  def singleString(row: JavaList[AnyRef]): String = {
    if (row.size() != 1) throw new RuntimeException("Expected row to contain only a single string, was: " + row)
    row.get(0).asInstanceOf[String]
  }

  def singleLong(row: JavaList[AnyRef]): Long = {
    if (row.size() != 1) throw new RuntimeException("Expected row to contain only a single long, was: " + row)
    row.get(0).asInstanceOf[Long]
  }

  def longAndString(row: JavaList[AnyRef]): (Long, String) = {
    if (row.size() != 2) throw new RuntimeException("Expected row to contain long and a string, was: " + row)
    (row.get(0).asInstanceOf[Long], row.get(1).asInstanceOf[String])
  }

  def threeLongs(row: JavaList[AnyRef]): (Long, Long, Long) = {
    if (row.size() != 3) throw new RuntimeException("Expected row to contain 3 longs, was: " + row)
    (row.get(0).asInstanceOf[Long], row.get(1).asInstanceOf[Long], row.get(2).asInstanceOf[Long])
  }

  def tupleLongLong(row: JavaList[AnyRef]): (Long, Long) = {
    if (row.size() != 2) throw new RuntimeException("Expected row to contain 2 longs, was: " + row)
    (row.get(0).asInstanceOf[Long], row.get(1).asInstanceOf[Long])
  }

  def tupleStringStringLong(row: JavaList[AnyRef]): (String, String, Long) = {
    if (row.size() != 3) throw new RuntimeException("Expected row to contain 3 values, was: " + row)
    (row.get(0).asInstanceOf[String], row.get(1).asInstanceOf[String], row.get(2).asInstanceOf[Long])
  }

  def tupleLongString(row: JavaList[AnyRef]): (Long, String) = {
    if (row.size() != 2) throw new RuntimeException("Expected row to contain 2 values, was: " + row)
    (row.get(0).asInstanceOf[Long], row.get(1).asInstanceOf[String])
  }

  def tupleStringLong(row: JavaList[AnyRef]): (String, Long) = {
    if (row.size() != 2) throw new RuntimeException("Expected row to contain 2 values, was: " + row)
    (row.get(0).asInstanceOf[String], row.get(1).asInstanceOf[Long])
  }

  private def datomify(target: Any): AnyRef = {
    target match {
      case seq: Seq[Any] => datomifySeq(seq)
      case map: Map[_, _] => datomifyMap(map)
      case x => x.asInstanceOf[AnyRef]
    }
  }

  private def datomifySeq(seq: Seq[Any]) = {
    val datomifiedSeq = seq.map(datomify)
    toJavaList(datomifiedSeq)
  }

  private def datomifyMap(map: Map[_, _]) = {
    val datomifiedMap = map.map(datomifyEntry)
    toJavaMap(datomifiedMap)
  }

  private def datomifyEntry(entry: (Any, Any)) = {
    val (key, value) = entry
    val datomifiedEntry = (datomify(key), datomify(value))
    datomifiedEntry
  }

  private def toJavaMap(map: Map[AnyRef, AnyRef]): JavaMap[AnyRef, AnyRef] = {
    val javaMap = new util.HashMap[AnyRef, AnyRef]
    def addEntry(entry: (AnyRef, AnyRef)) {
      val (key, value) = entry
      javaMap.put(key, value)
    }
    map.foreach(addEntry)
    javaMap
  }

  private def toJavaList(seq: Seq[AnyRef]): JavaList[AnyRef] = {
    val javaList = new util.ArrayList[AnyRef]
    def addEntry(entry: AnyRef) {
      javaList.add(entry)
    }
    seq.foreach(addEntry)
    javaList
  }
}
