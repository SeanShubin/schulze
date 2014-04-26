package com.seanshubin.schulze.persistence

import datomic.{Util, Entity, Connection, Peer}
import datomic.Util._
import java.util.Collection
import java.util.{List => JavaList}
import scala.collection.JavaConversions
import datomic.function.Function

object Prototype extends App {
  try {
    run()
    System.exit(0)
  } catch {
    case ex: Throwable =>
      ex.printStackTrace()
      System.exit(1)
  }

  def run() {
    val uri: String = "datomic:mem://foo"
    Peer.createDatabase(uri)
    val connection: Connection = Peer.connect(uri)

    def transact(textWithMargin: String) {
      val text = textWithMargin.stripMargin
      println("transaction:")
      println(text)
      connection.transact(list(read(text))).get()
    }

    def listToSeq(list: JavaList[AnyRef]): Seq[AnyRef] = JavaConversions.collectionAsScalaIterable(list).toSeq

    def query(textWithMargin: String): Seq[Seq[AnyRef]] = {
      val text = textWithMargin.stripMargin
      println("query:")
      println(text)
      val results: Collection[JavaList[AnyRef]] = Peer.q(read(text), connection.db)
      println(s"${results.size()} results")
      val resultsIterable: Iterable[JavaList[AnyRef]] = JavaConversions.collectionAsScalaIterable(results)
      resultsIterable.foreach(println)
      val resultsSeq: Seq[JavaList[AnyRef]] = resultsIterable.toSeq
      resultsSeq.map(listToSeq)
    }

    def entityToMap(entity:Entity):Map[String, AnyRef] = {
      val keys = JavaConversions.asScalaSet(entity.keySet())
      val entries = for {
        key <- keys
      } yield {
        (key, entity.get(key))
      }
      val map = entries.toMap
      map
    }

    transact(
      """{
        |  :db/id                 #db/id[:db.part/db]
        |  :db/ident              :election/name
        |  :db/valueType          :db.type/string
        |  :db/cardinality        :db.cardinality/one
        |  :db.install/_attribute :db.part/db
        |}
        |""")
    transact(
      """{
        |  :db/id #db/id[:db.part/user]
        |  :db/ident :createElection
        |  :db/fn #db/fn {
        |    :lang "java"
        |    :params [dbObject nameObject]
        |    :code "import java.util.Collection;
        |           import java.util.List;
        |           Database db = (Database) dbObject;
        |           String name = (String) nameObject;
        |           String query = \"[:find ?election :in $ ?electionName :where \" +
        |                   \"[?election :election/name ?electionName]]\";
        |           Collection<List<Object>> rows = q(query, db, name);
        |           if(rows.size() > 0) return list();
        |           else return list(map(
        |                   \":db/id\", tempid(\":db.part/user\"),
        |                   \":election/name\", name));"
        |  }
        |}""".stripMargin)

    query("[:find ?election ?electionName :where [?election :election/name ?electionName]]")
    transact("""[:createElection "Election A"]""")
    query("[:find ?election ?electionName :where [?election :election/name ?electionName]]")
    transact("""[:createElection "Election B"]""")
    query("[:find ?election ?electionName :where [?election :election/name ?electionName]]")
    transact("""[:createElection "Election A"]""")
    query("[:find ?election ?electionName :where [?election :election/name ?electionName]]")
  }
}
