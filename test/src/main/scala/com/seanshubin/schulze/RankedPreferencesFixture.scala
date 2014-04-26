package com.seanshubin.schulze

import com.seanshubin.black_box_test_5.Fixture

class RankedPreferencesFixture extends Fixture {
  def run(input: Iterable[String]): Iterable[String] = {
    val entries = input.map(Parser.parseCandidateAndRankEntry).toSeq
    val candidateNames = entries.map(Parser.nameFromCandidateAndRank)
    val rankingMap: Map[String, Int] = Map(entries: _*)
    val preferenceStrengths = PreferenceStrengths.fromRanks(candidateNames, rankingMap)
    Formatter.preferenceStrengthsToMultipleLineString(candidateNames, preferenceStrengths)
  }
}
