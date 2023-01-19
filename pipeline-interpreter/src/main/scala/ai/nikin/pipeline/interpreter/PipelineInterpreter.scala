package ai.nikin.pipeline.interpreter

import ai.nikin.pipeline.interpreter.Definition.{
  DeltaLakeTableDefinition,
  SparkAggregatorDefinition
}
import ai.nikin.pipeline.model.DSL.AggregationFunction.{Avg, Count, Max, Min, Sum}
import ai.nikin.pipeline.model.DSL._
import scalax.collection.edges.DiEdge

object PipelineInterpreter {
  def process(pipeline: PipelineDef): Seq[Definition] =
    pipeline
      .foldLeft(Map.empty[String, Definition])(
        { case (acc, vertex) => processVertex(acc)(vertex) },
        { case (acc, edge) => processEdge(acc, edge) }
      )
      .values
      .toSeq

  // TODO refactor duplicated code
  private def processEdge(
      acc:  Map[String, Definition],
      edge: DiEdge[Vertex[_]]
  ): Map[String, Definition] =
    edge match {
      case DiEdge(lake: Lake[_], a: Aggregation[_, _]) => acc.get(a.name) match {
          case Some(definition: SparkAggregatorDefinition) => acc +
              (a.name -> definition.copy(inputTable = lake.name))
          case Some(definition)                            => throw new RuntimeException(
              s"Definition $definition for ${a.name} doesn't match requested type"
            )
          case _                                           => throw new RuntimeException(s"Definition for ${a.name} couldn't be found")
        }
      case DiEdge(a: Aggregation[_, _], lake: Lake[_]) => acc.get(a.name) match {
          case Some(definition: SparkAggregatorDefinition) => acc +
              (a.name -> definition.copy(outputTable = lake.name))
          case Some(definition)                            => throw new RuntimeException(
              s"Definition $definition for ${a.name} doesn't match requested type"
            )
          case _                                           => throw new RuntimeException(s"Definition for ${a.name} couldn't be found")
        }
      case _                                           => acc
    }

  private def processVertex(acc: Map[String, Definition]): Vertex[_] => Map[String, Definition] = {
    case l: Lake[_] if acc.contains(l.name) => acc
    case l: Lake[_]                         =>
      val ddl = DeltaLakeDDLGenerator.generateDDL(l.name)(l.schema)
      acc + (l.name -> DeltaLakeTableDefinition(l.name, ddl))
    case a: Aggregation[_, _]               =>
      val aggregationDefinition =
        SparkAggregatorDefinition(
          a.name,
          "__input_placeholder__",
          "__output_placeholder__",
          toSparkFunction(a.aggFunction),
          a.aggFunction.inputColumn,
          a.aggFunction.outputColumn
        )

      acc + (a.name -> aggregationDefinition)
    case _ => acc
  }

  private def toSparkFunction: AggregationFunction => String = {
    case _: Avg   => "avg"
    case _: Max   => "max"
    case _: Min   => "min"
    case _: Sum   => "sum"
    case _: Count => "count"
  }
}
