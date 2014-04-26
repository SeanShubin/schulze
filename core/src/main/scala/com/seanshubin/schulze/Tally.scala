package com.seanshubin.schulze

class Tally(val candidateNames: Seq[String], votes: Map[String, Map[String, Long]]) {
  val voterNames: Seq[String] = votes.keys.toSeq.sorted
  val preferences: Map[String, Map[String, Long]] = Strengths.fromVotes(votes)
  val strongestPathResolution = computeStrongestPathResolution
  val strongestPaths = Util.computeStrongestPreferences(candidateNames, preferences, strongestPathResolution.lastPaths)
  val rankings = Util.computeRankings(candidateNames, strongestPaths)
  val noSupport = Util.computeNoSupport(candidateNames, votes)
  /*
  def toMultipleLineString: Seq[String] = {
    import Formatter.indent
    Seq("Candidates") ++ indent(candidateNames) ++
      Seq("Preference Strengths") ++ indent(Formatter.preferenceStrengthsToMultipleLineString(candidateNames, preferenceStrengths)) ++
      Seq("Preference Strength Matrix") ++ indent(Formatter.preferenceStrengthsToGrid(candidateNames, preferenceStrengths)) ++
      Formatter.toMultipleLineString(strongestPathResolution, preferenceStrengths) ++
      Seq("Strengths of the strongest paths") ++ indent(Formatter.preferenceStrengthsToMultipleLineString(candidateNames, strongestPreferences)) ++
      Seq("Strongest Path Matrix") ++ indent(Formatter.preferenceStrengthsToGrid(candidateNames, strongestPreferences)) ++
      Seq("Ranking") ++ indent(Formatter.formatRankings(rankings))
  }
*/
  private def computeStrongestPathResolution: StrongestPathResolution = {
    val initialPaths = Util.initialPaths(candidateNames)
    val initialResolution = StrongestPathResolution(initialPaths, Seq())
    val resolution = candidateNames.foldLeft(initialResolution)(exploreAlternative)
    resolution
  }

  private def exploreAlternative(resolution: StrongestPathResolution, name: String): StrongestPathResolution = {
    val currentPaths = resolution.lastPaths
    val alternatePaths = Util.alternatives(currentPaths, name)
    val strongestPaths = Util.resolveStrongestPaths(preferences, currentPaths, alternatePaths)
    val alternatePathExploration = AlternatePathExploration(name, alternatePaths, strongestPaths)
    val strongestPathResolution = resolution.addAlternatePathExploration(alternatePathExploration)
    strongestPathResolution
  }
}
