package com.seanshubin.schulze

case class AlternatePathExploration(candidate: String,
                                    alternatePaths: Seq[Seq[String]],
                                    strongestPaths: Seq[Seq[String]])
