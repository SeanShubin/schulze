package com.seanshubin.schulze.server.data_transfer

import com.seanshubin.schulze.domain.Ranking

case class CandidateDescriptionRank(candidate:String, description:Option[String], rank:Option[Int])

object CandidateDescriptionRank {
  def apply( ranking: Ranking):CandidateDescriptionRank = {
    new CandidateDescriptionRank(ranking.candidateName, ranking.maybeCandidateDescription, ranking.rank.map(_.toInt))
  }
}
