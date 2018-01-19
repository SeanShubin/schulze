package com.seanshubin.schulze

object Vote {
  def normalizeVote(candidates:Seq[String], rankings:Map[String, Long]):Map[String, Long] = {
    val sortedDistinctRanks = rankings.values.toSeq.distinct.sorted
    def toMapping(entry:(Long, Int)):(Option[Long], Long) = {
      val (rank, index) = entry
      val mapping = Some(rank) -> (index + 1L)
      mapping
    }
    val oldToNewRankingEntries =
      sortedDistinctRanks.zipWithIndex.map(toMapping) :+
        (None -> (sortedDistinctRanks.size+1L))
    val oldToNewRanking = oldToNewRankingEntries.toMap
    def normalizedRankingForCandidate(candidate:String):(String, Long) = {
      val normalizedRanking = candidate -> oldToNewRanking(rankings.get(candidate))
      normalizedRanking
    }
    val normalized = candidates.map(normalizedRankingForCandidate).toMap
    normalized
  }
  def normalizeVotes(candidates:Seq[String], votes: Map[String, Map[String, Long]]):Map[String, Map[String, Long]] = {
    def normalizeVoteEntry(voteEntry: (String, Map[String, Long])) = {
      val (voter, vote) = voteEntry
      val result = voter -> normalizeVote(candidates, vote)
      result
    }
    votes.map(normalizeVoteEntry)
  }
}
