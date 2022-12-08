package ai.nikin.pipeline.interpreter

sealed trait Definition {
  def name: String
}

object Definition {
  case class LakeDefinition(name: String, ddl: String) extends Definition

  case class AggregationDefinition(
      name:         String,
      inputTable:   String,
      outputTable:  String,
      function:     String,
      inputColumn:  String,
      outputColumn: String
  ) extends Definition
}
