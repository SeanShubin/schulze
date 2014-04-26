package com.seanshubin.schulze

import com.seanshubin.black_box_test_5.Fixture

class OrderedPreferencesFixture extends Fixture {
  def run(input: Iterable[String]): Iterable[String] = {
    val candidates = input.toSeq
    val preferenceStrengths = PreferenceStrengths.fromOrdered(candidates)
    Formatter.preferenceStrengthsToMultipleLineString(candidates, preferenceStrengths)
  }
}
