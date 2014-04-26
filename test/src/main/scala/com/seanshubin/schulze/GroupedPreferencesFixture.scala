package com.seanshubin.schulze

import com.seanshubin.black_box_test_5.Fixture

class GroupedPreferencesFixture extends Fixture {
  def run(input: Iterable[String]): Iterable[String] = {
    val groups: Seq[Seq[String]] = input.map(Parser.stringToPath).toSeq
    val candidates = groups.flatten
    val preferenceStrengths = PreferenceStrengths.fromOrderedGroups(candidates, groups)
    Formatter.preferenceStrengthsToMultipleLineString(candidates, preferenceStrengths)
  }
}
