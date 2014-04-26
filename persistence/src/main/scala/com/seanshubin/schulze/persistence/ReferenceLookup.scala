package com.seanshubin.schulze.persistence

import datomic.Database
import com.seanshubin.schulze.persistence.DatomicUtil._
import com.seanshubin.schulze.persistence.datomic_util.ScalaAdaptor._


object ReferenceLookup {
  case class CandidateRow(candidate: Ref, name: String)

  object CandidateRow {
    def fromRow(row: DatomicRow) = CandidateRow(row.get(0).asInstanceOf[Long], row.get(1).asInstanceOf[String])
  }

  case class RankingRow(ranking: Ref, candidate: Ref, rank: Long)

  object RankingRow {
    def fromRow(row: DatomicRow) = RankingRow(row.get(0).asInstanceOf[Long], row.get(1).asInstanceOf[Long], row.get(2).asInstanceOf[Long])
  }

  def lookupElection(db: Database, electionName: String): Long = {
    val query =
      "[:find ?election :in $ ?electionName :where " +
        "[?election :election/name ?electionName]]"
    val election = querySingleRow(singleLong, query, db, electionName)
    election
  }

  def countElection(db: Database, electionName: String): Long = {
    val query =
      "[:find ?election :in $ ?electionName :where " +
        "[?election :election/name ?electionName]]"
    val count = countRows(query, db, electionName)
    count
  }

  def lookupCandidate(db: Database, election: Long, candidateName: String): Long = {
    val query =
      "[:find ?candidate :in $ ?election ?candidateName :where " +
        "[?candidate :candidate/election ?election]" +
        "[?candidate :candidate/name ?candidateName]]"
    val candidate = querySingleRow(singleLong, query, db, election, candidateName)
    candidate
  }

  def lookupCandidateAndDescription(db: Database, election: Long, candidateName: String): (Long, Option[String]) = {
    val query =
      "[:find ?candidate ?candidateDescription :in $ ?election ?candidateName :where " +
        "[?candidate :candidate/election    ?election]" +
        "[?candidate :candidate/name        ?candidateName]" +
        "[?candidate :candidate/description ?candidateDescription]]"
    val rows:Seq[(Long, String)] = queryRows(tupleLongString, query, db, election, candidateName)
    if(rows.size == 1) {
      val (candidate, description) = rows.head
      val tuple = (candidate, Some(description))
      tuple
    } else {
      val tuple = (lookupCandidate(db, election, candidateName), None)
      tuple
    }
  }

  def countCandidate(db: Database, election: Long, candidateName: String): Long = {
    val query =
      "[:find ?candidate :in $ ?election ?candidateName :where " +
        "[?candidate :candidate/election ?election]" +
        "[?candidate :candidate/name ?candidateName]]"
    val count = countRows(query, db, election, candidateName)
    count
  }

  def lookupRankingIdsByCandidate(db: Database, candidate: Long): Seq[Long] = {
    val query =
      "[:find ?ranking :in $ ?candidate :where " +
        "[?ranking :ranking/candidate ?candidate]]"
    val rankings = queryRows(singleLong, query, db, candidate)
    rankings
  }

  def lookupVoter(db: Database, voterName: String): Long = {
    val query =
      "[:find ?voter :in $ ?voterName :where " +
        "[?voter :voter/name ?voterName]]"
    val voter = querySingleRow(singleLong, query, db, voterName)
    voter
  }

  def countVoter(db: Database, voterName: String): Long = {
    val query =
      "[:find ?voter :in $ ?voterName :where " +
        "[?voter :voter/name ?voterName]]"
    val count = countRows(query, db, voterName)
    count
  }

  def lookupCandidates(db: Database, election: Long): Seq[CandidateRow] = {
    val query =
      "[:find ?candidate ?candidateName :in $ ?election :where " +
        "[?candidate :candidate/election ?election]" +
        "[?candidate :candidate/name ?candidateName]]"
    val rows = queryRows(longAndString, query, db, election)
    rows.map(tuple => (CandidateRow.apply _).tupled(tuple))
  }

  def lookupCandidateIds(db: Database, election: Long): Seq[Long] = {
    val query =
      "[:find ?candidate :in $ ?election :where " +
        "[?candidate :candidate/election ?election]]"
    val candidateIds = queryRows(singleLong, query, db, election)
    candidateIds
  }

  def lookupRankings(db: Database, election: Long, voter: Long): Seq[RankingRow] = {
    val query =
      "[:find ?ranking ?candidate ?rank :in $ ?election ?voter :where " +
        "[?ranking   :ranking/election  ?election ]" +
        "[?ranking   :ranking/voter     ?voter    ]" +
        "[?ranking   :ranking/candidate ?candidate]" +
        "[?ranking   :ranking/rank      ?rank     ]]"
    val rows = queryRows(threeLongs, query, db, election, voter)
    rows.map(tuple => (RankingRow.apply _).tupled(tuple))
  }

  def lookupRankingIdsByVoter(db: Database, voter: Long): Seq[Long] = {
    val query =
      "[:find ?ranking :in $ ?voter :where " +
        "[?ranking   :ranking/voter ?voter ]]"
    val rows = queryRows(singleLong, query, db, voter)
    rows
  }

  def lookupRankingIdsByElection(db: Database, election: Long): Seq[Long] = {
    val query =
      "[:find ?ranking :in $ ?election :where " +
        "[?ranking   :ranking/election  ?election ]]"
    val rows = queryRows(singleLong, query, db, election)
    rows
  }
}
