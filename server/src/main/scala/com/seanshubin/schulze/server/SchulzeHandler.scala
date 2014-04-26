package com.seanshubin.schulze.server

import com.seanshubin.server._
import com.seanshubin.schulze.server.data_transfer._
import com.seanshubin.schulze._
import com.seanshubin.schulze.persistence.{PersistenceSnapshotApi, PersistenceApi}
import com.seanshubin.schulze.server.data_transfer.Candidate
import com.seanshubin.schulze.server.data_transfer.AlternativesAndPaths
import com.seanshubin.schulze.server.data_transfer.TallyDto
import com.seanshubin.schulze.server.data_transfer.Voter
import com.seanshubin.server.SimplifiedRequest
import com.seanshubin.server.Content
import com.seanshubin.server.SimplifiedResponse
import com.seanshubin.schulze.StrongestPathResolution
import com.seanshubin.schulze.server.data_transfer.Election
import com.seanshubin.schulze.AlternatePathExploration

class SchulzeHandler(jsonSerialization: JsonSerialization,
                     persistence: PersistenceApi,
                     displayOrdering: DisplayOrdering) extends SimplifiedHandler {
  val lock = new Object()

  def handle(request: SimplifiedRequest): Option[SimplifiedResponse] = {
    import Verb._

    val maybeResponse = lock.synchronized {
      //should really let datomic handle synchronization, for now, serialize it at the http level
      request match {
        case Post("elections") => createElection(request)
        case Post("elections", electionName, "candidates") => createCandidate(request, electionName)
        case Get("elections") => getElections
        case Get("elections", electionName, "candidates") => getCandidates(electionName)
        case Get("elections", name) => getElection(name)
        case Post("voters") => createVoter(request)
        case Get("voters") => getVoters
        case Get("voters", name) => getVoter(name)
        case Delete("voters", name) => deleteVoter(name)
        case Delete("elections", name) => deleteElection(name)
        case Delete("elections", electionName, "candidates", candidateName) => deleteCandidate(electionName, candidateName)
        case Get("favicon.ico") => favicon()
        case Get("elections", electionName, "candidates", candidateName) => getCandidate(electionName, candidateName)
        case Get("vote") => getVote(request)
        case Put("vote") => putVote(request)
        case Patch("elections", electionName, "candidates", candidateName) => patchCandidate(request, electionName, candidateName)
        case Get("places", electionName) => getPlaces(request, electionName)
        case Get("tally", electionName) => getTally(request, electionName)
        case Options("elections") => emptyOkResponse
        case _ => None
      }
    }
    maybeResponse
  }

  def cleanName(name: String): String = {
    name.trim.replaceAll( """\s+""", " ")
  }

  private def createElection(request: SimplifiedRequest): Option[SimplifiedResponse] = {
    val Election(electionName) = jsonSerialization.fromJson(request.body, classOf[Election])
    persistence.createElection(cleanName(electionName))
    emptyOkResponse
  }

  private def createCandidate(request: SimplifiedRequest, electionName: String): Option[SimplifiedResponse] = {
    val CandidateName(candidateName) = jsonSerialization.fromJson(request.body, classOf[CandidateName])
    persistence.createCandidate(electionName, cleanName(candidateName))
    emptyOkResponse
  }

  private def patchCandidate(request: SimplifiedRequest, electionName: String, candidateName: String): Option[SimplifiedResponse] = {
    val Candidate(candidateName, maybeDescription) = jsonSerialization.fromJson(request.body, classOf[Candidate])
    persistence.updateCandidate(electionName, candidateName, maybeDescription)
    emptyOkResponse
  }

  private def getElections: Option[SimplifiedResponse] = {
    val snapshot = persistence.snapshot
    val elections = displayOrdering.sortElections(snapshot.electionNames()).map(Election.apply)
    val body = jsonSerialization.toJson(elections)
    okJsonResponse(body)
  }

  private def getCandidates(electionName: String): Option[SimplifiedResponse] = {
    val snapshot = persistence.snapshot
    if (snapshot.electionNames().contains(electionName)) {
      val candidates = displayOrdering.sortCandidates(snapshot.candidates(electionName))
      val body = jsonSerialization.toJson(candidates)
      okJsonResponse(body)
    } else {
      notFoundResponse
    }
  }

  private def createVoter(request: SimplifiedRequest): Option[SimplifiedResponse] = {
    val Voter(voterName) = jsonSerialization.fromJson(request.body, classOf[Voter])
    persistence.createVoter(cleanName(voterName))
    emptyOkResponse
  }

  private def deleteVoter(name: String) = {
    val snapshot = persistence.snapshot
    if (snapshot.voterNames().contains(name)) {
      persistence.deleteVoter(name)
      emptyOkResponse
    }
    else notFoundResponse
  }

  private def deleteElection(name: String) = {
    val snapshot = persistence.snapshot
    if (snapshot.electionNames().contains(name)) {
      persistence.deleteElection(name)
      emptyOkResponse
    }
    else notFoundResponse
  }

  private def getVoter(name: String) = {
    val snapshot = persistence.snapshot
    if (snapshot.voterNames().contains(name)) okJsonResponse(jsonSerialization.toJson(Voter(name)))
    else notFoundResponse
  }

  private def getElection(name: String) = {
    val snapshot = persistence.snapshot
    if (snapshot.electionNames().contains(name)) okJsonResponse(jsonSerialization.toJson(Election(name)))
    else notFoundResponse
  }

  private def getCandidate(electionName: String, candidateName: String) = {
    val snapshot = persistence.snapshot
    val maybeCandidate = snapshot.candidate(electionName, candidateName)
    val response = maybeCandidate match {
      case Some(candidate) =>
        okJsonResponse(jsonSerialization.toJson(candidate))
      case None => notFoundResponse
    }
    response
  }

  private def deleteCandidate(electionName: String, candidateName: String) = {
    val snapshot = persistence.snapshot
    val maybeCandidate = snapshot.candidate(electionName, candidateName)
    val response = maybeCandidate match {
      case Some(candidate) =>
        persistence.deleteCandidate(electionName, candidateName)
        emptyOkResponse
      case None => notFoundResponse
    }
    response
  }


  private def getVoters: Option[SimplifiedResponse] = {
    val snapshot = persistence.snapshot
    val voters = displayOrdering.sortVoters(snapshot.voterNames()).map(Voter.apply)
    val body = jsonSerialization.toJson(voters)
    okJsonResponse(body)
  }

  private def getVote(request: SimplifiedRequest): Option[SimplifiedResponse] = {
    val snapshot = persistence.snapshot
    val parameters = request.parametersAsMapOfFirstOccurrence()
    val voterName = parameters("voter")
    val electionName = parameters("election")
    if (snapshot.electionNames().contains(electionName) && snapshot.voterNames().contains(voterName)) {
      val rankings = snapshot.vote(electionName, voterName).map(CandidateDescriptionRank.apply)
      val sortedRankings = displayOrdering.sortRankings(rankings)
      val body = jsonSerialization.toJson(sortedRankings)
      okJsonResponse(body)
    } else {
      notFoundResponse
    }
  }

  def rankingsFromMap(rankingsMap: Map[String, Long]): Map[String, Option[Long]] = {
    def convertEntry(oldEntry: (String, Long)): (String, Option[Long]) = {
      val (key, value) = oldEntry
      val newEntry = key -> Some(value.asInstanceOf[Long])
      newEntry
    }
    val ranking = rankingsMap.map(convertEntry)
    ranking
  }

  private def putVote(request: SimplifiedRequest): Option[SimplifiedResponse] = {
    val parameters = request.parametersAsMapOfFirstOccurrence()
    val voterName = parameters("voter")
    val electionName = parameters("election")
    val rankings = CandidateRank.seqToMap(jsonSerialization.fromJsonArray(request.body, classOf[CandidateRank]))
    persistence.updateVote(electionName, voterName, CandidateRank.removeOptionNone(rankings))
    emptyOkResponse
  }

  private def getPlaces(request: SimplifiedRequest, electionName: String): Option[SimplifiedResponse] = {
    val snapshot = persistence.snapshot
    if (snapshot.electionNames().contains(electionName)) {
      val tallyResult = computeTally(snapshot, electionName)
      okJsonResponse(jsonSerialization.toJson(tallyResult.places))
    } else {
      notFoundResponse
    }
  }

  private def getTally(request: SimplifiedRequest, electionName: String): Option[SimplifiedResponse] = {
    val snapshot = persistence.snapshot
    if (snapshot.electionNames().contains(electionName)) {
      val tallyResult = computeTally(snapshot, electionName)
      okJsonResponse(jsonSerialization.toJson(tallyResult))
    } else {
      notFoundResponse
    }
  }

  def computeTally(snapshot: PersistenceSnapshotApi, electionName: String): TallyDto = {
    val candidateNames = snapshot.candidates(electionName).map(_.name).toSeq.sorted
    val votes = Vote.normalizeVotes(candidateNames, snapshot.votes(electionName))
    val tally = new Tally(candidateNames, votes)
    val places = tally.rankings
    val voterNames = tally.voterNames
    val preferences = Strengths.fillBlanksWithZeroes(candidateNames, tally.preferences)
    val floydWarshall = strongestPathResolutionToFloydWarshall(tally.strongestPathResolution, tally.preferences)
    val strongestPaths = tally.strongestPaths

    TallyDto(places, candidateNames.toSeq, voterNames, votes, preferences, floydWarshall, strongestPaths)
  }

  private def strongestPathResolutionToFloydWarshall(strongestPathResolution: StrongestPathResolution, preferences: Map[String, Map[String, Long]]): Map[String, AlternativesAndPaths] = {
    def addWeights(path: Seq[String]): Seq[Any] = {
      if (path.isEmpty) Seq()
      else if (path.size == 1) Seq(path)
      else {
        val segments = path.sliding(2).toVector
        def expandSegment(segment: Seq[String]): Seq[Any] = {
          val Seq(winner, loser) = segment
          val strength = Strengths.strength(preferences, winner, loser)
          Seq(winner, strength, loser)
        }
        def getTail(x: Seq[Any]) = x.tail
        val expanded: Seq[Seq[Any]] = segments.map(expandSegment).toSeq.toVector
        val result = (expanded.head +: expanded.tail.map(getTail)).flatten
        result
      }
    }
    def toEntry(exploration: AlternatePathExploration): (String, AlternativesAndPaths) = {
      val candidateName = exploration.candidate
      val alternatives = exploration.alternatePaths.map(addWeights)
      val paths = exploration.strongestPaths.map(addWeights)
      candidateName -> AlternativesAndPaths(alternatives, paths)
    }
    val entries = strongestPathResolution.alternateExplorations.map(toEntry)
    entries.toMap
  }

  private def okJsonResponse(body: String): Option[SimplifiedResponse] = {
    val code = HttpResponseCode.Ok.code
    val mediaType = InternetMediaType.Json.name
    val content = Content(mediaType, body)
    Some(SimplifiedResponse(code, Some(content), headers))
  }

  private def favicon(): Option[SimplifiedResponse] = Some(SimplifiedResponse(HttpResponseCode.NoContent.code))

  private val headers: Seq[(String, String)] = Seq(
    ("Content-Type", InternetMediaType.Json.name)
  )
  private val emptyOkResponse = Some(SimplifiedResponse(HttpResponseCode.Ok.code, None, headers))
  private val notFoundResponse = Some(SimplifiedResponse(HttpResponseCode.NotFound.code, None, headers))
}
