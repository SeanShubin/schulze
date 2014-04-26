package com.seanshubin.schulze.domain

case class Candidate(name:String, description:Option[String])

object Candidate {
  def apply(name:String):Candidate = Candidate(name, None)
}
