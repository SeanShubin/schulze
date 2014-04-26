package com.seanshubin.schulze.commands

import com.seanshubin.schulze.{Tally, SchulzeVotingSystem}

class SchulzeInterpreterImpl extends SchulzeInterpreter {
  private var votingSystem: SchulzeVotingSystem = SchulzeVotingSystem()

  def addVoter(name: String) {
    votingSystem = votingSystem.addVoter(name)
  }

  def addElection(name: String) {
    votingSystem = votingSystem.addElection(name)
  }

  def inElectionAddCandidate(electionName: String, candidateName: String) {
    votingSystem = votingSystem.addCandidate(electionName, candidateName)
  }

  def inElectionVoterCasts(electionName: String, voterName: String, candidateNames: Seq[String]) {
    votingSystem = votingSystem.castVote(voterName, electionName, candidateNames)
  }

  def tallyElection(electionName: String): Seq[String] = {
    val election = votingSystem.elections.electionsByName(electionName)
    val talliedElection: Tally = election.tally()
    talliedElection.toMultipleLineString
  }
}
