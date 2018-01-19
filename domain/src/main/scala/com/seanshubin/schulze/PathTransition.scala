package com.seanshubin.schulze

case class PathTransition(strength: Int, name: String) {
  override def toString: String = s"($strength)-$name"
}
