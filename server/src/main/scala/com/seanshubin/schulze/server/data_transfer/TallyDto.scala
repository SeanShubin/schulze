package com.seanshubin.schulze.server.data_transfer

case class TallyDto(places: Seq[Seq[String]],
                    candidateNames: Seq[String],
                    voterNames: Seq[String],
                    votes: Map[String, Map[String, Long]],
                    preferences: Map[String, Map[String, Long]],
                    floydWarshall: Map[String, AlternativesAndPaths],
                    strongestPaths: Map[String, Map[String, Long]])
