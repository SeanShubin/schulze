package com.seanshubin.schulze

import com.seanshubin.black_box_test_5.Fixture
import com.seanshubin.black_box_test_5.script.CommandInterpreter
import com.seanshubin.schulze.commands.{SchulzeInterpreterImpl, SchulzeInterpreter}

class SchulzeScriptRunnerFixture extends Fixture {
  def run(input: Iterable[String]): Iterable[String] = {
    val schulzeInterpreter: SchulzeInterpreter = new SchulzeInterpreterImpl
    val commandInterpreter = new CommandInterpreter(classOf[SchulzeInterpreter], schulzeInterpreter)
    val output = input.flatMap(commandInterpreter.executeCommand)
    output
  }
}
