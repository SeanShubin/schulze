package com.seanshubin.schulze.persistence

import java.util.{Collection => JavaCollection, List => JavaList, Map => JavaMap}
import scala.collection.JavaConverters._
import datomic.{Connection, Util, Peer}
import com.seanshubin.schulze.persistence.datomic_util.ScalaAdaptor._

object DatomicUtil {
  type Ref = Long
  type DatomicRow = JavaList[AnyRef]
  type DatomicRows = JavaCollection[DatomicRow]
  type DatomicTransaction = AnyRef
  type DatomicTransactions = JavaList[DatomicTransaction]

  def extractString(row: DatomicRow): String = {
    if (row.size != 1) throw new RuntimeException("Expected only a single value in the row")
    row.get(0).asInstanceOf[String]
  }

  def extractLong(row: DatomicRow): Long = {
    if (row.size != 1) throw new RuntimeException("Expected only a single value in the row")
    row.get(0).asInstanceOf[java.lang.Long]
  }

  def extractLongFromRows(rows: DatomicRows): Long = {
    if (rows.size() != 1) throw new RuntimeException("Expected only a single row")
    val row = rows.asScala.iterator.next()
    if (row.size() != 1) throw new RuntimeException("Expected only a single value in the row")
    row.get(0) match {
      case x: java.lang.Long => x
      case _ => throw new RuntimeException("Expected a value of type long")
    }
  }

  def rowsToStringSet(rows: DatomicRows): Set[String] = {
    rows.asScala.map(extractString).toSet
  }

  def rowsToLongSet(rows: DatomicRows): Set[Long] = {
    rows.asScala.map(extractLong).toSet
  }

  def rowsToIterable(rows: DatomicRows): Iterable[DatomicRow] = rows.asScala

  def transactionsToIterable(transactions: DatomicTransactions): Iterable[DatomicTransaction] = transactions.asScala

  def tempId() = Peer.tempid(":db.part/user")

  def deleteEntities(connection: Connection, entities: Seq[Long]) {
    def retractEntity(id: Long) = Seq(":db.fn/retractEntity", id)
    val retractEntityTransactions = entities.map(retractEntity)
    transact(connection, retractEntityTransactions).get()
  }
}
