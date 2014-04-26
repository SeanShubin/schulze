package com.seanshubin.schulze

import com.seanshubin.black_box_test_5.Fixture
import com.seanshubin.black_box_test_5.tree.StringTree

class RankingFixture extends Fixture {
  def run(input: Iterable[String]): Iterable[String] = {
    val inputTree = StringTree.fromLines(input)
    val candidates = inputTree.sequenceAt("candidates")
    val preferenceStrengthLines = inputTree.sequenceAt("preference strengths")
    val preferenceStrengths = preferenceStrengthLines.map(Parser.parsePreferenceStrengthEntry).toMap
    val rankings: Seq[Seq[String]] = Util.computeRankings(candidates, preferenceStrengths)
    val outputLines = rankings.map(Formatter.pathToString)
    outputLines
  }
}
