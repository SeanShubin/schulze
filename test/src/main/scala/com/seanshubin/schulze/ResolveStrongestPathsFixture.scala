package com.seanshubin.schulze

import com.seanshubin.black_box_test_5.Fixture
import com.seanshubin.black_box_test_5.tree.StringTree

class ResolveStrongestPathsFixture extends Fixture {
  def run(input: Iterable[String]): Iterable[String] = {
    val inputTree = StringTree.fromLines(input)
    val preferenceLines = inputTree.sequenceAt("preference strengths")
    val preferenceStrengths = preferenceLines.map(Util.stringToPreferenceStrength).toMap
    val pathLines = inputTree.sequenceAt("paths")
    val paths = pathLines.map(Parser.stringToPath)
    val alternativeLines = inputTree.sequenceAt("alternatives")
    val alternatives = alternativeLines.map(Parser.stringToPath)
    val strongestPaths = Util.resolveStrongestPaths(preferenceStrengths, paths, alternatives)
    val strongestPathStrings = strongestPaths.map(Formatter.pathToString)
    strongestPathStrings
  }
}
