package com.seanshubin.schulze.server

import com.seanshubin.server._
import java.nio.charset.{Charset, StandardCharsets}
import org.eclipse.jetty.server.Handler
import com.seanshubin.schulze.persistence._
import scala.util.Random
import datomic.Connection

trait SchulzeWiring {
  def port: Int

  def datomicUri: String

  def random:Random

  lazy val charset:Charset = StandardCharsets.UTF_8
  lazy val resourceLoader:ResourceLoader = new ResourceLoaderImpl(charset)
  lazy val connectionProvider: ConnectionProvider = new ConnectionProviderImpl(datomicUri, resourceLoader)
  lazy val connection: Connection = connectionProvider.connection
  lazy val persistenceApi:PersistenceApi = new DatomicPersistenceApi(connection)
  lazy val randomness:Randomness = new RandomnessImpl(random)
  lazy val displayOrdering:DisplayOrdering = new DisplayOrderingImpl(randomness)
  lazy val schulzeApiHandler: SimplifiedHandler = new SchulzeHandler(jsonSerialization, persistenceApi, displayOrdering)
  lazy val simplifiedTransformer: SimplifiedTransformer = new SimplifiedTransformerImpl(charset)
  lazy val jsonSerialization: JsonSerialization = new JsonSerializationImpl
  lazy val notifications: Notifications = new LineEmittingNotifications(println)
  lazy val lastHandler: SimplifiedHandler = new LastHandler(jsonSerialization)
  lazy val compositeHandler = new CompositeHandler(Seq(schulzeApiHandler, testHandler, lastHandler))
  lazy val testHandler: SimplifiedHandler = new TestHandler(jsonSerialization)
  lazy val classLoaderHandler: Handler = new SchulzeClassLoaderHandler()
  lazy val serverErrorHandler = new ServerErrorHandlerImpl(jsonSerialization)
  lazy val jettyHandler: Handler = new SimplifiedHandlerAdaptor(simplifiedTransformer, compositeHandler, notifications, serverErrorHandler)
  lazy val welcomePageHandler: Handler = new WelcomePageHandler()
  lazy val handlers: Handler = new CompositeJettyHandler(Seq(welcomePageHandler, classLoaderHandler, jettyHandler))
  lazy val server: HttpServer = new JettyServer(port, handlers)
}
