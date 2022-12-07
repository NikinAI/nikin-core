package ai.nikin.pipeline.interpreter

sealed trait Definition

object Definition {
  case class LakeDefinition(ddl: String) extends Definition

  case class AggregationDefinition(
      inputTable:   String,
      outputTable:  String,
      function:     String,
      inputColumn:  String,
      outputColumn: String
  ) extends Definition
}
