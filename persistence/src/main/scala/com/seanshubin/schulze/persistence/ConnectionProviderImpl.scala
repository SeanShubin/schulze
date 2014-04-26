package com.seanshubin.schulze.persistence

import datomic.{Util, Peer, Connection}

class ConnectionProviderImpl(datomicUri: String, resourceLoader:ResourceLoader) extends ConnectionProvider {
  lazy val connection: Connection = {
    Peer.createDatabase(datomicUri)
    val theConnection = Peer.connect(datomicUri)
    val schema = resourceLoader.withReaderFor("/schema.edn")(Util.readAll)
    theConnection.transact(schema).get()
    theConnection
  }
}
