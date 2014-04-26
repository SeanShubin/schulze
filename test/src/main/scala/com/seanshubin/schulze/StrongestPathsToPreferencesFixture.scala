package com.seanshubin.schulze

import com.seanshubin.black_box_test_5.Fixture
import com.seanshubin.black_box_test_5.tree.StringTree

class StrongestPathsToPreferencesFixture extends Fixture {
  def run(input: Iterable[String]) = {
    val inputTree = StringTree.fromLines(input)
    val candidates = inputTree.sequenceAt("candidates")
    val preferenceStrengthLines = inputTree.sequenceAt("preference strengths")
    val preferenceStrengths = preferenceStrengthLines.map(Parser.parsePreferenceStrengthEntry).toMap
    val strongestPathLines = inputTree.sequenceAt("strongest paths")
    val strongestPaths = strongestPathLines.map(Parser.stringToPath)
    val strongestPreferences = Util.computeStrongestPreferences(candidates, preferenceStrengths, strongestPaths)
    Formatter.preferenceStrengthsToMultipleLineString(candidates, strongestPreferences)
  }
}
