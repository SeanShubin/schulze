package com.seanshubin.schulze.persistence

import datomic.{Connection, Peer}
import ReferenceLookup._
import com.seanshubin.schulze.persistence.datomic_util.ScalaAdaptor
import ScalaAdaptor._

class DatomicPersistenceApi(connection: Connection) extends PersistenceApi {
  def createElection(name: String) {
    val db = connection.db()
    if(countElection(db, name) > 0) return
    val electionId = Peer.tempid(":db.part/user")
    val election = Map(
      ":db/id" -> electionId,
      ":election/name" -> name)
    transact(connection, Seq(election))
  }

  def deleteElection(name: String) {
    val db = connection.db()
    val election: Long = lookupElection(db, name)
    val candidates: Seq[Long] = lookupCandidateIds(db, election)
    val rankings: Seq[Long] = lookupRankingIdsByElection(db, election)
    val entities: Seq[Long] = Seq(election) ++ candidates.toSeq ++ rankings.toSeq
    DatomicUtil.deleteEntities(connection, entities)
  }

  def createVoter(name: String) {
    val db = connection.db()
    if(countVoter(db, name) > 0) return
    val voterId = Peer.tempid(":db.part/user")
    val voter = Map(
      ":db/id" -> voterId,
      ":voter/name" -> name)
    transact(connection, Seq(voter))
  }

  def deleteVoter(name: String) {
    val db = connection.db()
    val voter: Long = lookupVoter(db, name)
    val rankings: Seq[Long] = lookupRankingIdsByVoter(db, voter)
    val entities: Seq[Long] = Seq(voter) ++ rankings.toSeq
    DatomicUtil.deleteEntities(connection, entities)
  }

  def createCandidate(electionName: String, candidateName: String) {
    val db = connection.db()
    val candidateId = Peer.tempid(":db.part/user")
    val election = lookupElection(db, electionName)
    if(countCandidate(db, election, candidateName) > 0) return
    val candidate = Map(
      ":db/id" -> candidateId,
      ":candidate/name" -> candidateName,
      ":candidate/election" -> election)
    transact(connection, Seq(candidate))
  }

  def updateCandidate(electionName: String, candidateName: String, maybeDescription: Option[String]) {
    val db = connection.db()
    val election = lookupElection(db, electionName)
    val (candidate, maybeOriginalDescription) = lookupCandidateAndDescription(db, election, candidateName)
    maybeDescription match {
      case Some(description) =>
        val candidateMap = Map(
        ":db/id" -> candidate,
        ":candidate/description" -> description)
        transact(connection, Seq(candidateMap))
      case None => maybeOriginalDescription match {
        case Some(originalDescription) =>
          val redaction = Seq(":db/retract", candidate, ":candidate/description", originalDescription)
          transact(connection, Seq(redaction))
        case None =>
          //do nothing
      }
    }
  }

  def deleteCandidate(electionName: String, candidateName: String) {
    val db = connection.db()
    val election: Long = lookupElection(db, electionName)
    val candidate: Long = lookupCandidate(db, election, candidateName)
    val rankings: Seq[Long] = lookupRankingIdsByCandidate(db, candidate)
    val entities: Seq[Long] = Seq(candidate) ++ rankings.toSeq
    DatomicUtil.deleteEntities(connection, entities)
  }

  def updateVote(electionName: String, voterName: String, rankingsByCandidateName: Map[String, Long]) {
    UpdateVoteHelper.updateVote(connection, electionName, voterName, rankingsByCandidateName)
  }

  def resetVote(electionName: String, voterName: String) {
    updateVote(electionName, voterName, Map())
  }

  def snapshot: PersistenceSnapshotApi = new DatomicPersistenceSnapshotApi(connection.db())
}
