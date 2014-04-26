package com.seanshubin.schulze

import com.seanshubin.black_box_test_5.Fixture

class InitialPathsFixture extends Fixture {
  def run(input: Iterable[String]): Iterable[String] = {
    val candidates = input.toSeq
    val paths = Util.initialPaths(candidates)
    val output = paths.map(_.mkString(" "))
    output
  }
}
