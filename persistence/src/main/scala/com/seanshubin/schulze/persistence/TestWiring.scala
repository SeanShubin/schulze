package com.seanshubin.schulze.persistence

import datomic.Connection
import java.nio.charset.{Charset, StandardCharsets}

trait TestWiring {
  def datomicUri: String
  lazy val charset:Charset = StandardCharsets.UTF_8
  lazy val resourceLoader:ResourceLoader = new ResourceLoaderImpl(charset)
  lazy val connectionProvider:ConnectionProvider = new ConnectionProviderImpl(datomicUri, resourceLoader)
  lazy val connection: Connection = connectionProvider.connection
  lazy val persistenceApi: PersistenceApi = new DatomicPersistenceApi(connection)
}
