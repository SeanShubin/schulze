package com.seanshubin.schulze.commands

trait SchulzeInterpreter {
  def addVoter(name: String)

  def addElection(name: String)

  def inElectionAddCandidate(electionName: String, candidateName: String)

  def inElectionVoterCasts(electionName: String, voterName: String, candidateNames: Seq[String])

  def tallyElection(electionName: String): Seq[String]
}
