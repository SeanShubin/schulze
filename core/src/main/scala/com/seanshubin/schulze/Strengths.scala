package com.seanshubin.schulze

object Strengths {
  type LossStrengths = Map[String, Long]
  type WinStrengths = Map[String, LossStrengths]
  type Ranking = (String, Long)
  type Rankings = Map[String, Long]
  type Votes = Map[String, Rankings]
  type Preference = (String, String)
  type PreferenceStrength = (String, String, Long)
  val emptyWinStrengths =Map[String, LossStrengths]()
  val emptyLossStrengths =Map[String, Long]()

  def isPreferred(rankings:Rankings, win:String, loss:String):Boolean = {
    (rankings.get(win), rankings.get(loss)) match {
      case (Some(winRank), Some(lossRank)) => winRank < lossRank
      case _ => false
    }
  }

  def strength(winStrengths:WinStrengths, win:String, loss:String):Long = {
    winStrengths.get(win) match {
      case Some(lossStrengths) => lossStrengths.get(loss) match {
        case Some(strength) => strength
        case None => 0
      }
      case None => 0
    }
  }

  def fromRankings(rankings:Rankings):WinStrengths = {
    val preferences = for {
      winner <- rankings.keys
      loser <- rankings.keys
      if winner != loser
      if isPreferred(rankings, winner, loser)
    } yield {
      (winner, loser)
    }
    fromPreferences(preferences)
  }

  def fromPreferences(preferences:Iterable[Preference]):WinStrengths = {
    def toPreferenceStrengthOne(preference:Preference):PreferenceStrength = {
      val (win, loss) = preference
      val preferenceStrengthOne = (win, loss, 1L)
      preferenceStrengthOne
    }
    fromPreferenceStrengths(preferences.map(toPreferenceStrengthOne))
  }

  def fromPreferenceStrengths(preferenceStrengths:Iterable[PreferenceStrength]):WinStrengths = {
    def addPreferenceStrength(soFar:WinStrengths, preferenceStrength:PreferenceStrength):WinStrengths = {
      val (win, loss, strength) = preferenceStrength
      val oldLossStrengths:LossStrengths = soFar.getOrElse(win, Map())
      val newLossStrengths:LossStrengths = oldLossStrengths + (loss -> strength)
      val newSoFar = soFar + (win -> newLossStrengths)
      newSoFar
    }
    val strengths = preferenceStrengths.foldLeft(emptyWinStrengths)(addPreferenceStrength)
    strengths
  }

  def fromVotes(votes:Votes):WinStrengths = {
    val individualStrengths = votes.values.map(fromRankings)
    val summedStrengths = individualStrengths.foldLeft(emptyWinStrengths)(addStrengths)
    summedStrengths
  }

  def addStrengths(left:WinStrengths, right:WinStrengths):WinStrengths = {
    val wins = (left.keys ++ right.keys).toSet
    val winStrengthEntries = for {
      win <- wins
    } yield win -> addMaybeLossStrengths(left.get(win), right.get(win))
    val winStrengths = winStrengthEntries.toMap
    winStrengths
  }

  def addMaybeLossStrengths(maybeLeft:Option[LossStrengths], maybeRight:Option[LossStrengths]):LossStrengths = {
    (maybeLeft, maybeRight) match {
      case (Some(left), Some(right)) => addLossStrengths(left, right)
      case (Some(left), None) => left
      case (None, Some(right)) => right
      case (None, None) => emptyLossStrengths
    }
  }

  def addLossStrengths(left:LossStrengths, right:LossStrengths):LossStrengths = {
    val losses = (left.keys ++ right.keys).toSet
    val lossStrengthEntries = for {
      loss <- losses
    } yield loss -> addMaybeStrengths(left.get(loss), right.get(loss))
    val lossStrengths = lossStrengthEntries.toMap
    lossStrengths
  }

  def addMaybeStrengths(maybeLeft:Option[Long], maybeRight:Option[Long]):Long = {
    (maybeLeft, maybeRight) match {
      case (Some(left), Some(right)) => left + right
      case (Some(left), None) => left
      case (None, Some(right)) => right
      case (None, None) => 0
    }
  }

  def fillBlanksWithZeroes(candidates:Seq[String], strengths:WinStrengths):WinStrengths = {
    val preferenceStrengths = for {
      win <- candidates
      loss <- candidates
      if win != loss
      strength = Strengths.strength(strengths, win, loss)
    } yield {
      (win, loss, strength)
    }
    fromPreferenceStrengths(preferenceStrengths)
  }
}
