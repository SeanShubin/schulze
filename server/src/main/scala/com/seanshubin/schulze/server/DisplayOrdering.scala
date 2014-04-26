package com.seanshubin.schulze.server

import com.seanshubin.schulze.domain.Candidate
import com.seanshubin.schulze.server.data_transfer.CandidateDescriptionRank

trait DisplayOrdering {
  def sortElections(elections:Seq[String]):Seq[String]
  def sortVoters(voters:Seq[String]):Seq[String]
  def sortCandidates(candidates:Seq[Candidate]):Seq[Candidate]
  def sortRankings(rankings:Seq[CandidateDescriptionRank]):Seq[CandidateDescriptionRank]
}
