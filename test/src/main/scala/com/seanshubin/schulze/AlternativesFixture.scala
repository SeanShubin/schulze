package com.seanshubin.schulze

import com.seanshubin.black_box_test_5.Fixture
import com.seanshubin.black_box_test_5.tree.StringTree

class AlternativesFixture extends Fixture {
  def run(input: Iterable[String]): Iterable[String] = {
    val inputTree = StringTree.fromLines(input)
    val pathsLines = inputTree.sequenceAt("paths")
    val paths = pathsLines.map(Parser.stringToPath)
    val pivot = inputTree.singleValueAt("pivot")
    val alternatives = Util.alternatives(paths, pivot)
    val output = alternatives.map(Formatter.pathToString)
    output
  }
}
