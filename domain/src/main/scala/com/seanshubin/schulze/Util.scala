package com.seanshubin.schulze

import scala.annotation.tailrec

object Util {
  def computeNoSupport(candidates:Seq[String],  votes: Map[String, Map[String, Long]]):Seq[String] = {
    if(candidates.size <= 1) Seq()
    else {
      def noSupportForCandidate(candidate:String):Boolean = {
        def isWin(otherCandidate:String, rankings:Map[String, Long]):Boolean = {
          (rankings.get(candidate), rankings.get(otherCandidate)) match {
            case (Some(left), Some(right)) => left < right
            case (None, Some(right)) => false
            case (Some(left), None) => true
            case (None, None) => false
          }
        }
        val wins = for {
          (voter, rankings) <- votes
          (otherCandidate, rank) <- rankings
        } yield {
          isWin(otherCandidate, rankings)
        }
        val noSupport= wins.forall(!_)
        noSupport
      }
      val candidatesWithoutSupport = candidates.filter(noSupportForCandidate)
      candidatesWithoutSupport
    }
  }

  def computeRankings(candidates: Seq[String], strongestPaths: Map[String, Map[String, Long]]): Seq[Seq[String]] = {
    def strengthOf(winner: String, loser: String): Long = {
      Strengths.strength(strongestPaths,winner, loser)
    }
    @tailrec
    def innermostUnbeatenSet(currentSet: Seq[String]): Seq[String] = {
      def isUndefeated(candidate: String): Boolean = {
        def doesNotDefeatCandidate(opponent: String) = candidate == opponent || strengthOf(candidate, opponent) >= strengthOf(opponent, candidate)
        currentSet.forall(doesNotDefeatCandidate)
      }
      val nextSet: Seq[String] = currentSet.filter(isUndefeated)
      if (nextSet == currentSet) nextSet
      else innermostUnbeatenSet(nextSet)
    }
    @tailrec
    def loop(unranked: Seq[String], reversedRankings: List[Seq[String]]): Seq[Seq[String]] = {
      if (unranked.isEmpty) reversedRankings.reverse
      else {
        val undefeated: Seq[String] = innermostUnbeatenSet(unranked)
        val newUnranked = unranked.filterNot(candidate => undefeated.contains(candidate))
        loop(newUnranked, undefeated :: reversedRankings)
      }
    }
    loop(candidates, List())
  }

  def computeStrongestPreferences(candidates: Seq[String], preferenceStrengths: Map[String, Map[String, Long]], strongestPaths: Seq[Seq[String]]): Map[String, Map[String, Long]] = {
    def linkStrength(winner: String, loser: String): Long = {
      Strengths.strength(preferenceStrengths, winner, loser)
    }
    def computePathStrength(path: Seq[String]): Long = {
      val initialStrength = linkStrength(path(0), path(1))
      @tailrec
      def loop(weakestLink: Long, remainingPath: List[String]): Long = {
        val head :: tail = remainingPath
        if (tail.isEmpty) weakestLink
        else {
          val nextStrength = linkStrength(head, tail.head)
          val nextWeakestLink = math.min(weakestLink, nextStrength)
          loop(nextWeakestLink, tail)
        }
      }
      val finalStrength = loop(initialStrength, path.toList.tail)
      finalStrength
    }
    def pathToPreferenceStrength(path: Seq[String]): (String, String, Long) = {
      val winner = path.head
      val loser = path.last
      val pathStrength = computePathStrength(path)
      (winner, loser, pathStrength)
    }
    val strongestPreferences: Map[String, Map[String, Long]] = Strengths.fromPreferenceStrengths(strongestPaths.map(pathToPreferenceStrength))
    strongestPreferences
  }

  def resolveStrongestPaths(preferenceStrengths: Map[String, Map[String, Long]],
                            currentPaths: Seq[Seq[String]],
                            alternativePaths: Seq[Seq[String]]): Seq[Seq[String]] = {
    val alternativePathEntries = alternativePaths.map(pathToPathEntry)
    val alternativeMap = alternativePathEntries.toMap
    def replaceIfAlternativeStronger(path: Seq[String]): Seq[String] = {
      val preference = pathToPreference(path)
      alternativeMap.get(preference) match {
        case Some(alternativePath) =>
          if (pathStrength(preferenceStrengths, alternativePath) > pathStrength(preferenceStrengths, path)) alternativePath
          else path
        case None => path
      }
    }
    val newPaths = currentPaths.map(replaceIfAlternativeStronger)
    newPaths
  }

  def pathStrengthToString(pathStrength: (Seq[String], Long)): String = {
    val (path, strength) = pathStrength
    val stringValue = strength.toString + " " + path.mkString(" ")
    stringValue
  }

  val word = """(\w+)"""
  val spaces = """\s+"""
  val preferenceLinePattern = word + spaces + ">" + spaces + word + spaces + "=" + spaces + word
  val PreferenceLineRegex = preferenceLinePattern.r

//  def pathStrengths(preferenceStrengths: Seq[(String, String, Long)], paths: Seq[Seq[String]]): Seq[(Seq[String], Long)] = {
//    val preferenceStrengthMap = preferenceStrengths.toMap
//    def pathStrength(path: Seq[String]): (Seq[String], Long) = {
//      @tailrec
//      def loop(weakestLinkSoFar: Long, remainingPath: List[String]): Long = {
//        remainingPath match {
//          case winner :: loser :: _ =>
//            val preference = Preference(winner, loser)
//            val nextLink = preferenceStrengthMap(preference)
//            val weakestLink = math.min(weakestLinkSoFar, nextLink)
//            loop(weakestLink, remainingPath.tail)
//          case _ => weakestLinkSoFar
//        }
//      }
//      val pathList = path.toList
//      val winner :: loser :: _ = pathList
//      val initialStrength = preferenceStrengthMap(Preference(winner, loser))
//      val strength = loop(initialStrength, pathList.tail)
//      (path, strength)
//    }
//    val pathStrengthsResult = paths.map(pathStrength)
//    pathStrengthsResult
//  }

  def pathStrength(preferenceStrengths: Map[String, Map[String, Long]], path: Seq[String]): Long = {
    def pathStrength(path: Seq[String]): Long = {
      @tailrec
      def loop(weakestLinkSoFar: Long, remainingPath: List[String]): Long = {
        remainingPath match {
          case win :: loss :: _ =>
            val nextLink = Strengths.strength(preferenceStrengths, win, loss)
            val weakestLink = math.min(weakestLinkSoFar, nextLink)
            loop(weakestLink, remainingPath.tail)
          case _ => weakestLinkSoFar
        }
      }
      val pathList = path.toList
      val winner :: loser :: _ = pathList
      val initialStrength = Strengths.strength(preferenceStrengths, winner, loser)
      val strength = loop(initialStrength, pathList.tail)
      strength
    }
    val strength = pathStrength(path)
    strength
  }

//  def stringToPreferenceStrength(line: String): (Preference, Long) = {
//    val PreferenceLineRegex(winner, loser, strengthString) = line
//    val preference = Preference(winner, loser)
//    val strength = strengthString.toLong
//    val preferenceStrength = (preference, strength)
//    preferenceStrength
//  }
//
  def initialPaths(candidates: Seq[String]): Seq[Seq[String]] = {
    val paths = for {
      winner <- candidates
      loser <- candidates
      if winner != loser
    } yield Seq(winner, loser)
    paths
  }

  def pathsToPathEntries(paths: Seq[Seq[String]]): Seq[((String, String), Seq[String])] = paths.map(pathToPathEntry)

  def pathToPreference(path: Seq[String]): (String, String) = {
    val winner = path.head
    val loser = path.last
    val preference = (winner, loser)
    preference
  }

  def pathToPathEntry(path: Seq[String]): ((String, String), Seq[String]) = {
    val win = path.head
    val loss = path.last
    val entry = ((win, loss), path)
    entry
  }

  def alternatives(paths: Seq[Seq[String]],
                   pivot: String): Seq[Seq[String]] = {
    val pathEntries = paths.map(pathToPathEntry)
    val alternatives: Seq[Seq[String]] = for {
      (preference, path) <- pathEntries
      (winner, loser) = preference
      if winner != pivot
      if loser != pivot
      pathsMap = pathEntries.toMap
      fromWinner = pathsMap((winner, pivot))
      toLoser = pathsMap((pivot, loser))
      value = fromWinner ++ toLoser.tail
    } yield {
      value
    }
    alternatives
  }
}
