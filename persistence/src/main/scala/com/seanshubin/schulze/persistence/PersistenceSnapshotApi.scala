package com.seanshubin.schulze.persistence

import com.seanshubin.schulze.domain.{Ranking, Candidate}

trait PersistenceSnapshotApi {
  def electionNames(): Seq[String]

  def voterNames(): Seq[String]

  def candidates(electionName: String): Seq[Candidate]

  def candidate(electionName: String, candidateName:String): Option[Candidate]

  def votes(electionName: String): Map[String, Map[String, Long]]

  def vote(electionName: String, voterName: String): Seq[Ranking]
}
