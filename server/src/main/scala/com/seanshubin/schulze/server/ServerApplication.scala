package com.seanshubin.schulze.server

import com.seanshubin.server.{Configuration, ConfigurationValidatorImpl, ConfigurationValidator}
import scala.util.Random

object ServerApplication extends App {
  def startServer(configuration: Configuration) {
    val wiring = new SchulzeWiring {
      val port = configuration.port
      val datomicUri = configuration.datomicUri
      val random = new Random()
    }
    wiring.notifications.configuration(configuration)
    wiring.server.start()
    wiring.persistenceApi.snapshot.electionNames() //warm up the database
    wiring.notifications.datomicReady()
    wiring.server.join()
  }

  def configureAndRun() {
    val configurationValidator: ConfigurationValidator = new ConfigurationValidatorImpl
    configurationValidator.validate(args) match {
      case Right(configuration) =>
        startServer(configuration)
      case Left(messageLines) =>
        messageLines.foreach(println)
    }
  }

  try {
    configureAndRun()
  } catch {
    case ex: Throwable =>
      ex.printStackTrace()
      System.exit(1)
  }
}
