package com.seanshubin.schulze.persistence

import datomic.Peer

object Test extends App {
  def createSubject() = new TestWiring() {
    lazy val datomicUri: String = {
      val uri = "datomic:mem://sample"
      Peer.deleteDatabase(uri)
      uri
    }
  }

  trait Assertion {
    def toMultipleLineString: Seq[String]
  }

  case class AssertEqual[T](expected: T, actual: T, message: String) extends Assertion {
    def toMultipleLineString: Seq[String] = {
      if (expected == actual) {
        Seq("success: " + message)
      } else {
        Seq(
          "failure: " + message,
          "  expected: " + expected,
          "  actual  : " + actual)
      }
    }
  }

  case class AssertEqualUnordered[T](expected: Seq[T], actual: Seq[T], message: String) extends Assertion {
    def toMultipleLineString: Seq[String] = {
      if (expected.toSet == actual.toSet) {
        Seq("success: " + message)
      } else {
        Seq(
          "failure: " + message,
          "  expected: " + expected,
          "  actual  : " + actual)
      }
    }
  }

  def testElection(): Seq[Assertion] = {
    val subject = createSubject()
    subject.persistenceApi.createElection("Election A")
    subject.persistenceApi.createElection("Election B")
    subject.persistenceApi.createElection("Election C")
    subject.persistenceApi.deleteElection("Election B")
    val actual = subject.persistenceApi.snapshot.electionNames()
    val expected = Seq("Election A", "Election C")
    Seq(AssertEqualUnordered(expected, actual, "elections"))
  }

  def testCandidate(): Seq[Assertion] = {
    val subject = createSubject()
    subject.persistenceApi.createElection("Election A")
    subject.persistenceApi.createCandidate("Election A", "Candidate A")
    subject.persistenceApi.createCandidate("Election A", "Candidate B")
    subject.persistenceApi.createCandidate("Election A", "Candidate C")
    subject.persistenceApi.deleteCandidate("Election A", "Candidate B")
    val actual = subject.persistenceApi.snapshot.candidates("Election A")
    val expected = Seq("Candidate A", "Candidate C")
    Seq(AssertEqualUnordered(expected, actual, "candidates"))
  }

  def testVoter(): Seq[Assertion] = {
    val subject = createSubject()
    subject.persistenceApi.createVoter("Voter A")
    subject.persistenceApi.createVoter("Voter B")
    subject.persistenceApi.createVoter("Voter C")
    subject.persistenceApi.deleteVoter("Voter B")
    val actual = subject.persistenceApi.snapshot.voterNames()
    val expected = Seq("Voter A", "Voter C")
    Seq(AssertEqualUnordered(expected, actual, "voters"))
  }

  def testVotesForElection(): Seq[Assertion] = {
    val subject = createSubject()
    subject.persistenceApi.createElection("Ice Cream")
    subject.persistenceApi.createCandidate("Ice Cream", "Vanilla")
    subject.persistenceApi.createCandidate("Ice Cream", "Chocolate")
    subject.persistenceApi.createCandidate("Ice Cream", "Strawberry")
    subject.persistenceApi.createVoter("Alice")
    subject.persistenceApi.createVoter("Bob")
    subject.persistenceApi.createVoter("Carol")
    subject.persistenceApi.createVoter("Dave")
    subject.persistenceApi.updateVote("Ice Cream", "Alice", Map("Vanilla" -> 1, "Chocolate" -> 2, "Strawberry" -> 4))
    subject.persistenceApi.updateVote("Ice Cream", "Bob", Map("Vanilla" -> 22, "Chocolate" -> 33, "Strawberry" -> 11))
    subject.persistenceApi.updateVote("Ice Cream", "Carol", Map("Vanilla" -> 1, "Chocolate" -> 2, "Strawberry" -> 2))
    subject.persistenceApi.updateVote("Ice Cream", "Dave", Map("Chocolate" -> 5))
    subject.persistenceApi.resetVote("Ice Cream", "Bob")
    val expected = Map(
      "Alice" -> Map("Vanilla" -> 1, "Chocolate" -> 2, "Strawberry" -> 4),
      "Carol" -> Map("Vanilla" -> 1, "Chocolate" -> 2, "Strawberry" -> 2),
      "Dave" -> Map("Chocolate" -> 5)
    )
    val actual = subject.persistenceApi.snapshot.votes("Ice Cream")
    Seq(AssertEqual(expected, actual, "votes for election"))
  }

  def testVote(): Seq[Assertion] = {
    val subject = createSubject()
    subject.persistenceApi.createElection("Ice Cream")
    subject.persistenceApi.createCandidate("Ice Cream", "Vanilla")
    subject.persistenceApi.createCandidate("Ice Cream", "Chocolate")
    subject.persistenceApi.createCandidate("Ice Cream", "Strawberry")
    subject.persistenceApi.createVoter("Alice")
    subject.persistenceApi.createVoter("Bob")
    subject.persistenceApi.createVoter("Carol")
    subject.persistenceApi.createVoter("Dave")
    subject.persistenceApi.updateVote("Ice Cream", "Alice", Map("Vanilla" -> 1, "Chocolate" -> 2, "Strawberry" -> 4))
    subject.persistenceApi.updateVote("Ice Cream", "Bob", Map("Vanilla" -> 22, "Chocolate" -> 33, "Strawberry" -> 11))
    subject.persistenceApi.updateVote("Ice Cream", "Carol", Map("Vanilla" -> 1, "Chocolate" -> 2, "Strawberry" -> 2))
    subject.persistenceApi.updateVote("Ice Cream", "Dave", Map("Chocolate" -> 5))
    subject.persistenceApi.resetVote("Ice Cream", "Bob")
    val expected = Map("Vanilla" -> 1, "Chocolate" -> 2, "Strawberry" -> 2)
    val actual = subject.persistenceApi.snapshot.vote("Ice Cream", "Carol")
    Seq(AssertEqual(expected, actual, "vote"))
  }

  def run(): Seq[String] = {
    val tests = testElection() ++ testCandidate() ++ testVoter() ++ testVotesForElection() ++ testVote()
    val lines = tests.flatMap(_.toMultipleLineString)
    lines
  }

  try {
    run().foreach(println)
    System.exit(0)
  } catch {
    case ex: Throwable => ex.printStackTrace()
  } finally {
    System.exit(1)
  }
}
