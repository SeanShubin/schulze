package com.seanshubin.server

class ConfigurationValidatorImpl extends ConfigurationValidator {
  def validate(args: Seq[String]): Either[Seq[String], Configuration] = {
    val syntax = Seq(
      "Expected exactly two parameter",
      "(1) port: a whole number",
      "(2) datomic uri: a string, such as datomic:free://localhost:4334/schulze")
    val parameterCount = args.size
    if (parameterCount == 2) {
      try {
        Right(Configuration(args(0).toInt, args(1).toString))
      } catch {
        case exception: NumberFormatException =>
          val message: String = s"Unable to parse value '${args(0)}' into a port number"
          Left(Seq(message) ++ syntax)
      }
    } else {
      val message: String = s"Exactly two parameters expected, got $parameterCount"
      Left(Seq(message) ++ syntax)
    }
  }
}
