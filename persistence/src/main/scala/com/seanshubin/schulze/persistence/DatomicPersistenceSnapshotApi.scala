package com.seanshubin.schulze.persistence

import datomic.{Entity, Database}
import com.seanshubin.schulze.persistence.datomic_util.ScalaAdaptor._
import com.seanshubin.schulze.domain.{Ranking, Candidate}
import java.lang.{Long=>JavaLong}

class DatomicPersistenceSnapshotApi(db: Database) extends PersistenceSnapshotApi {
  def electionNames(): Seq[String] = {
    val query = "[:find ?electionName :where [?election :election/name ?electionName]]"
    val rows = queryRows(singleString, query, db)
    rows
  }

  def voterNames(): Seq[String] = {
    val query = "[:find ?voterName :where [?voter :voter/name ?voterName]]"
    val rows = queryRows(singleString, query, db)
    rows
  }

  def candidates(electionName: String): Seq[Candidate] = {
    val candidateIds = lookupCandidateIds(electionName)
    val candidateDomainObjects = candidateIds.map(idToCandidate)
    candidateDomainObjects
  }

  private def lookupCandidateIds(electionName: String): Seq[Long] = {
    val query =
      "[:find ?candidate :in $ ?electionName :where " +
        "[?election  :election/name      ?electionName ]" +
        "[?candidate :candidate/election ?election     ]]"
    val candidateIds = queryRows(singleLong, query, db, electionName)
    candidateIds
  }

  private def idToCandidate(id:Long) = {
    val entity = db.entity(id)
    val name = entity.get(":candidate/name").asInstanceOf[String]
    val maybeDescription = Option(entity.get(":candidate/description").asInstanceOf[String])
    Candidate(name, maybeDescription)
  }

  def candidate(electionName: String, candidateName: String): Option[Candidate] = {
    val query =
      "[:find ?candidate :in $ ?electionName ?candidateName :where " +
        "[?election  :election/name         ?electionName        ]" +
        "[?candidate :candidate/election    ?election            ]" +
        "[?candidate :candidate/name        ?candidateName       ]]"
    val candidateRows = queryRows(singleLong, query, db, electionName, candidateName)
    val maybeCandidate = if(candidateRows.size == 1) {
      val candidate = idToCandidate(candidateRows.head)
      Some(candidate)
    } else if(candidateRows.size == 0){
      None
    } else {
      throw new RuntimeException(
        s"Expected exactly one candidate matching election $electionName " +
        s"and candidate $candidateName, got ${candidateRows.size}")
    }
    maybeCandidate
  }

  def votes(electionName: String): Map[String, Map[String, Long]] = {
    val query =
      "[:find ?voterName ?candidateName ?rank :in $ ?electionName :where " +
        "[?election  :election/name     ?electionName ]" +
        "[?ranking   :ranking/election  ?election     ]" +
        "[?ranking   :ranking/voter     ?voter        ]" +
        "[?voter     :voter/name        ?voterName    ]" +
        "[?ranking   :ranking/candidate ?candidate    ]" +
        "[?candidate :candidate/name    ?candidateName]" +
        "[?ranking   :ranking/rank      ?rank         ]]"
    val rows = queryRows(tupleStringStringLong, query, db, electionName)
    def addRow(soFar: Map[String, Map[String, Long]], row: (String, String, Long)): Map[String, Map[String, Long]] = {
      val (voterName, candidateName, rank)= row
      val oldRankings = soFar.getOrElse(voterName, Map())
      val newRankings = oldRankings + (candidateName -> rank)
      val newSoFar = soFar + (voterName -> newRankings)
      newSoFar
    }
    rows.foldLeft(Map[String, Map[String, Long]]())(addRow)
  }

  def vote(electionName: String, voterName: String): Seq[Ranking] = {
    val electionId:Long = querySingleRow(singleLong,
      "[:find ?election :in $ ?electionName :where [?election :election/name ?electionName]]", db, electionName)
    val candidateIdsQuery =
      "[:find ?candidate :in $ ?election :where " +
      "[?candidate :candidate/election ?election]]"
    val candidateAndRankQuery =
      "[:find ?candidate ?rank :in $ ?election ?voterName :where " +
        "[?voter   :voter/name        ?voterName]" +
        "[?ranking :ranking/candidate ?candidate]" +
        "[?ranking :ranking/election  ?election ]" +
        "[?ranking :ranking/voter     ?voter    ]" +
        "[?ranking :ranking/rank      ?rank     ]]"
    val rankByCandidateId:Map[Long, Long] = queryRows(tupleLongLong, candidateAndRankQuery, db, electionId, voterName).toMap
    val candidateIds:Seq[Long] = queryRows(singleLong, candidateIdsQuery, db, electionId)
    def candidateIdToRanking(candidateId:Long):Ranking = {
      val candidate:Entity = db.entity(candidateId)
      val name:String = candidate.get(":candidate/name").asInstanceOf[String]
      val maybeDescription:Option[String] = Option(candidate.get(":candidate/description").asInstanceOf[String])
      val maybeRank:Option[Long] = rankByCandidateId.get(candidateId)
      Ranking(name, maybeDescription, maybeRank)
    }
    val rankings = candidateIds.map(candidateIdToRanking)
    rankings
  }
}
