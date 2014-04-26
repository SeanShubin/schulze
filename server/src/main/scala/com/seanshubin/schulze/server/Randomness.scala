package com.seanshubin.schulze.server

trait Randomness {
  def shuffle[T](target:Seq[T]):Seq[T]
}
