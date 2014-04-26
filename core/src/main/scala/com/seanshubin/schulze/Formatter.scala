package com.seanshubin.schulze

object Formatter {
/*
  def formatRankings(rankings: Seq[Seq[String]]): Seq[String] = {
    def formatRank(rank: Seq[String]) = if (rank.size == 1) rank.head
    else {
      val rankString = rank.mkString(", ")
      s"tie between $rankString"
    }
    def formatRankWithIndex(rankWithIndex: (Seq[String], Int)): String = {
      val (rank, index) = rankWithIndex
      val placeString = formatPlace(index)
      val rankString = formatRank(rank)
      s"$placeString: $rankString"
    }
    rankings.zipWithIndex.map(formatRankWithIndex)
  }

  def formatPlace(index: Long): String = {
    val place = index + 1
    place match {
      case 1 => "1st"
      case 2 => "2nd"
      case 3 => "3rd"
      case x => "" + x + "th"
    }
  }

  def indent(lines: Seq[String]): Seq[String] = {
    def indentLine(line: String) = "    " + line
    lines.map(indentLine)
  }

  def pathToString(path: Seq[String]): String = path.mkString(" ")

  def toMultipleLineString(resolution: StrongestPathResolution, preferences: Map[Preference, Long]): Seq[String] = {
    def initialWeightedString = pathToWeightedPathString(_: Seq[String], preferences)
    def alternateWeightedString = alternateToMultipleLineString(_: AlternatePathExploration, preferences)
    val initialPaths = resolution.initialPaths.map(initialWeightedString)
    val alternatePaths = resolution.alternateExplorations.flatMap(alternateWeightedString)
    Seq("Detailed number crunching (warshall algorithm)") ++ indent(initialPaths) ++ alternatePaths
  }

  def alternateToMultipleLineString(alternatePathExploration: AlternatePathExploration, preferenceStrengths: Map[Preference, Long]): Seq[String] = {
    val pathToWeightedStringWithPreferenceStrengths = pathToWeightedPathString(_: Seq[String], preferenceStrengths)
    val AlternatePathExploration(candidate, alternatePaths, strongestPaths) = alternatePathExploration
    Seq(s"Alternatives using candidate $candidate") ++
      indent(alternatePaths.map(pathToWeightedStringWithPreferenceStrengths)) ++
      Seq(s"Strongest paths after resolving alternatives using $candidate") ++
      indent(strongestPaths.map(pathToWeightedStringWithPreferenceStrengths))
  }

  def preferenceStrengthsToGrid(candidateNames: Seq[String], preferenceStrengths: Map[Preference, Long]): Seq[String] = {
    def createRow(winner: String): Seq[String] = {
      def createCell(loser: String): String = {
        if (winner == loser) ""
        else {
          val preferenceCount = preferenceStrengths.getOrElse(Preference(winner, loser), 0)
          preferenceCount.toString
        }
      }
      val row: Seq[String] = winner +: candidateNames.map(createCell)
      row
    }
    val headerRow: Seq[String] = "" +: candidateNames
    val remainingRows: Seq[Seq[String]] = candidateNames.map(createRow)
    val allCells: Seq[Seq[String]] = headerRow +: remainingRows
    val lines: Seq[String] = StringGrid.alignCells(allCells)
    lines
  }

  def preferenceStrengthsToMultipleLineString(candidateNames: Seq[String], preferenceStrengths: Map[Preference, Long]): Seq[String] = {
    for {
      winner <- candidateNames
      loser <- candidateNames
      if winner != loser
      preference = Preference(winner, loser)
      preferenceCount = preferenceStrengths.getOrElse(preference, 0)
    } yield s"$winner > $loser = $preferenceCount"
  }

  def pathsToWeightedStrings(paths: Seq[Seq[String]], preferenceStrengths: Map[Preference, Long]): Seq[String] = {
    val pathToWeightedStringWithPreferenceStrengths = pathToWeightedPathString(_: Seq[String], preferenceStrengths)
    paths.map(pathToWeightedStringWithPreferenceStrengths)
  }

  def pathToWeightedPathString(path: Seq[String], preferences: Map[Preference, Long]): String = {
    case class WeightAndNext(weight: Long, candidate: String) {
      override def toString: String = s"-($weight)-$candidate"
    }
    case class WeightedPath(initial: String, remaining: Seq[WeightAndNext] = Seq()) {
      def add(candidate: String): WeightedPath = {
        val weight = preferences.getOrElse(Preference(last, candidate), 0L)
        val newRemaining = remaining :+ WeightAndNext(weight, candidate)
        copy(remaining = newRemaining)
      }

      def last: String = if (remaining.isEmpty) initial else remaining.last.candidate

      override def toString: String = initial + remaining.mkString
    }

    @tailrec
    def pathToWeightedPathLoop(soFar: WeightedPath, remainingCandidates: List[String]): WeightedPath = {
      if (remainingCandidates.isEmpty) soFar
      else pathToWeightedPathLoop(soFar.add(remainingCandidates.head), remainingCandidates.tail)
    }

    def pathToWeightedPath(path: Seq[String]): WeightedPath = {
      val pathList = path.toList
      pathToWeightedPathLoop(WeightedPath(pathList.head), pathList.tail)
    }

    pathToWeightedPath(path).toString
  }
   */
}
