package com.seanshubin.schulze.persistence

trait PersistenceApi {
  def createElection(name: String)

  def deleteElection(name: String)

  def createVoter(name: String)

  def deleteVoter(name: String)

  def createCandidate(electionName: String, candidateName: String)

  def updateCandidate(electionName: String, candidateName: String, maybeDescription:Option[String])

  def deleteCandidate(electionName: String, candidateName: String)

  def updateVote(electionName: String, voterName: String, rankings: Map[String, Long])

  def resetVote(electionName: String, voterName: String)

  def snapshot: PersistenceSnapshotApi
}
