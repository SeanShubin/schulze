package com.seanshubin.schulze

case class StrongestPathResolution(initialPaths: Seq[Seq[String]],
                                   alternateExplorations: Seq[AlternatePathExploration]) {
  def lastPaths: Seq[Seq[String]] =
    if (alternateExplorations.isEmpty) initialPaths
    else alternateExplorations.last.strongestPaths

  def addAlternatePathExploration(alternateExploration: AlternatePathExploration) =
    this.copy(alternateExplorations = alternateExplorations :+ alternateExploration)
}