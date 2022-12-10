package ai.nikin.pipeline.interpreter

sealed trait Definition {
  def name: String
}

object Definition {
  case class DeltaLakeTableDefinition(name: String, ddl: String) extends Definition

  case class SparkAggregatorDefinition(
      name:         String,
      inputTable:   String,
      outputTable:  String,
      function:     String,
      inputColumn:  String,
      outputColumn: String
  ) extends Definition
}
