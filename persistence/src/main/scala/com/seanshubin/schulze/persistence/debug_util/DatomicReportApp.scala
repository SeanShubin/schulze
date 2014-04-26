package com.seanshubin.schulze.persistence.debug_util

import datomic.Peer

object DatomicReportApp extends App {
  def run(): Seq[String] = {
    val datomicUri = "datomic:free://localhost:4334/schulze"
    val connection = Peer.connect(datomicUri)
    val db = connection.db
    DatomicReporter.report(db)
  }

  try {
    val lines = run()
    lines.foreach(println)
    System.exit(0)
  } catch {
    case ex:Throwable =>
      ex.printStackTrace()
      System.exit(1)
  }
}
