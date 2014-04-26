package com.seanshubin.schulze

import com.seanshubin.black_box_test_5.Fixture
import com.seanshubin.black_box_test_5.tree.StringTree

class PathStrengthFixture extends Fixture {
  def run(input: Iterable[String]): Iterable[String] = {
    val inputTree = StringTree.fromLines(input)
    val preferenceLines = inputTree.sequenceAt("preferences")
    val preferences = preferenceLines.map(Util.stringToPreferenceStrength)
    val pathLines = inputTree.sequenceAt("paths")
    val paths = pathLines.map(Parser.stringToPath)
    val pathStrengths = Util.pathStrengths(preferences, paths)
    val output = pathStrengths.map(Util.pathStrengthToString)
    output
  }
}
