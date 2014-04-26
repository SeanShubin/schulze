package com.seanshubin.schulze.server

import scala.util.Random

class RandomnessImpl(random:Random) extends Randomness {
  def shuffle[T](target:Seq[T]):Seq[T] = random.shuffle(target)
}
