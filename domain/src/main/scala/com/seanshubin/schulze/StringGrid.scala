package com.seanshubin.schulze

object StringGrid {
  def alignCells(rows: Seq[Seq[String]]): Seq[String] = {
    val columns: Seq[Seq[String]] = rows.transpose
    def columnWidth(cells: Seq[String]): Int = {
      val maxWidth = cells.map(_.size).max
      if (maxWidth == 0) 1 else maxWidth
    }
    val widths: Seq[Int] = columns.map(columnWidth)
    val widthAndColumnSeq: Seq[(Int, Seq[String])] = widths zip columns
    def makeSameSize(width: Int, column: Seq[String]): Seq[String] = {
      val format = s"%-${width}s"
      def pad(cell: String) = format.format(cell)
      column.map(pad)
    }
    val makeSameSizeFunction = makeSameSize _
    val makeSameSizeTupled = makeSameSizeFunction.tupled
    val paddedColumns = widthAndColumnSeq.map(makeSameSizeTupled)
    val paddedRows = paddedColumns.transpose
    def rowToLine(row: Seq[String]) = trimRight(row.mkString(" "))
    val lines = paddedRows.map(rowToLine)
    lines
  }

  def trimRight(target: String): String = {
    target.reverse.dropWhile(isSpace).reverse
  }

  def isSpace(char: Char): Boolean = char.isSpaceChar
}
