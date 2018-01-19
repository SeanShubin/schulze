package com.seanshubin.schulze

object Parser {
  /*
  private val word = """(\w+)"""
  private val spaces = """\s+"""
  private val preferenceLinePattern = word + spaces + ">" + spaces + word + spaces + "=" + spaces + word
  private val PreferenceLineRegex = preferenceLinePattern.r
  private val CandidateAndRank = """(\w+) (\d+)""".r

  def stringToPath(line: String): Seq[String] = line.split( """\s+""")

  def parsePreferenceStrengthEntry(line: String): (Preference, Int) = {
    val PreferenceLineRegex(winner, loser, quantityString) = line
    val quantity = quantityString.toInt
    Preference(winner, loser) -> quantity
  }

  def nameFromCandidateAndRank(candidateAndRank: (String, Int)) = {
    val (name, _) = candidateAndRank
    name
  }

  def parseCandidateAndRankEntry(line: String): (String, Int) = {
    val CandidateAndRank(candidateName, rankString) = line
    (candidateName, rankString.toInt)
  }
*/
}
