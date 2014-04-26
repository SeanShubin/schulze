package com.seanshubin.schulze.server

import scala.math.Ordering
import com.seanshubin.schulze.domain.Candidate
import com.seanshubin.schulze.server.data_transfer.CandidateDescriptionRank

class DisplayOrderingImpl(randomness:Randomness) extends DisplayOrdering {
  implicit object RankingOrdering extends Ordering[CandidateDescriptionRank] {
    def compare(x: CandidateDescriptionRank, y: CandidateDescriptionRank): Int = {
      (x.rank, y.rank) match {
        case (Some(xRank), Some(yRank)) =>
          if(xRank == yRank) x.candidate compare y.candidate
          else xRank compare yRank
        case (Some(rank), None) => -1
        case (None, Some(rank)) => 1
        case (None, None) => 0
      }
    }
  }

  def sortElections(elections:Seq[String]):Seq[String] = {
     elections.toSeq.sorted
   }
   def sortVoters(voters:Seq[String]):Seq[String] = {
     voters.toSeq.sorted
   }
   def sortCandidates(candidates:Seq[Candidate]):Seq[Candidate] = {
     randomness.shuffle(candidates)
   }
   def sortRankings(rankings:Seq[CandidateDescriptionRank]):Seq[CandidateDescriptionRank] = {
     def checkHasRank(ranking:CandidateDescriptionRank):Boolean = ranking.rank.isDefined
     val (hasRank, doesNotHaveRank) = rankings.partition(checkHasRank)
     val sortedRankings = hasRank.sorted ++ randomness.shuffle(doesNotHaveRank)
     sortedRankings
   }
 }
