package com.seanshubin.schulze.persistence

case class Column(table: String, attribute: String, columnType: ColumnType, cardinality: Cardinality = Cardinality.One)
