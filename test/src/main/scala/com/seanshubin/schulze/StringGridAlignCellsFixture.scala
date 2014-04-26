package com.seanshubin.schulze

import com.seanshubin.black_box_test_5.Fixture

class StringGridAlignCellsFixture extends Fixture {
  def run(input: Iterable[String]): Iterable[String] = {
    def createRowFromLine(line: String): Seq[String] = line.split(" ")
    val cells: Seq[Seq[String]] = input.toSeq.map(createRowFromLine)
    StringGrid.alignCells(cells)
  }
}
