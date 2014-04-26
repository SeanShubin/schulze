package com.seanshubin.schulze

import com.seanshubin.black_box_test_5.Fixture
import com.seanshubin.black_box_test_5.tree.StringTree

class PathsAsStringsFixture extends Fixture {
  def run(input: Iterable[String]): Iterable[String] = {
    val tree = StringTree.fromLines(input)
    val pathLines = tree.sequenceAt("paths")
    val paths = pathLines.map(Parser.stringToPath)
    val preferenceLines = tree.sequenceAt("preferences")
    val preferences = preferenceLines.map(Parser.parsePreferenceStrengthEntry).toMap
    val output = Formatter.pathsToWeightedStrings(paths, preferences)
    output
  }
}
