package ai.nikin.pipeline.interpreter

import ai.nikin.pipeline.interpreter.Definition.{AggregationDefinition, LakeDefinition}
import ai.nikin.pipeline.interpreter.BirdOperator.Pipe
import ai.nikin.pipeline.sdk.Flow
import ai.nikin.pipeline.sdk.{Aggregation, Lake}
import ai.nikin.typedgraph.core.{AnyEdge, AnyVertex, Edge, Graph}

object BirdOperator extends Serializable {

  implicit class Pipe[A](a: A) {
    def |>[Z](f: A => Z): Z = f(a)
  }

  implicit class Pipe2[A, B](a: (A, B)) {
    def |>[Z](f: (A, B) => Z): Z = f.tupled(a)
  }

}

object PipelineInterpreter {
  def process(pipeline: Graph): Map[String, Definition] =
    pipeline.foldEdgeLeft(Map.empty[String, Definition]) {
      case (acc, Edge.Triplet(from @ Lake(n), edge @ Flow(a, b), to @ Aggregation(name, _))) =>
        // Process Lake to aggregation
        processVertex(acc)(from) |> (processEdge(_)(Set(from), edge))

      case (acc, Edge.Triplet(a @ Aggregation(name, _), edge, to @ Lake(n))) =>
        // process Lake
        processVertex(updateAggregationOutput(acc, a, to))(to)

      case (acc, e) => throw new Exception(s"Unknown $e")
    }

  private def updateAggregationOutput(
      acc:   Map[String, Definition],
      value: Aggregation[Any, Any],
      to:    Lake[_]
  ): Map[String, Definition] =
    acc.get(value.label) match {
      case Some(definition: AggregationDefinition) => acc +
          (value.label -> definition.copy(outputTable = to.name))
      case _                                       => acc
    }

  private def processEdge(acc: Map[String, Definition])(
      ancestors:               Set[AnyVertex],
      edge:                    AnyEdge
  ): Map[String, Definition] =
    edge match {
      case Flow(_, a @ Aggregation(_, aggFunction)) if !acc.contains(a.label) =>
        val aggregationDefinition =
          AggregationDefinition(
            ancestors.head.asInstanceOf[Lake[_]].name,
            "__output_placeholder__",
            aggFunction.getClass.getSimpleName,
            aggFunction.inputColumn,
            aggFunction.outputColumn
          )

        acc + (a.label -> aggregationDefinition)
      case _                                                                  => acc
    }

  private def processVertex(acc: Map[String, Definition]): AnyVertex => Map[String, Definition] = {
    case l: Lake[_] if acc.contains(l.label) => acc
    case l: Lake[_]                          =>
      val ddl = DeltaLakeDDLGenerator.generateDDL(l.schema)
      acc + (l.label -> LakeDefinition(ddl))
    case _ => acc
  }
}