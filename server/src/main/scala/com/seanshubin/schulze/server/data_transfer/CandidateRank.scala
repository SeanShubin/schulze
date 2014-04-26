package com.seanshubin.schulze.server.data_transfer

case class CandidateRank(candidate:String, rank:Option[Int])

object CandidateRank{
  def removeOptionNone(map: Map[String, Option[Long]]): Map[String, Long] = {
    for {
      (key, maybeValue) <- map
      if maybeValue.isDefined
    } yield {
      key -> maybeValue.get
    }
  }

  def seqToMap(rankings: Seq[CandidateRank]): Map[String, Option[Long]] = {
    def discardUnlessPositive(maybeNumber: Option[Int]) = maybeNumber match {
      case Some(number) => if (number < 1L) None else Some(number)
      case None => None
    }
    val entries = for {
      CandidateRank(candidate, rank) <- rankings
    } yield {
      (candidate, discardUnlessPositive(rank).map(_.toLong))
    }
    entries.toMap
  }
}
