package com.seanshubin.server

trait ConfigurationValidator {
  def validate(args: Seq[String]): Either[Seq[String], Configuration]
}
