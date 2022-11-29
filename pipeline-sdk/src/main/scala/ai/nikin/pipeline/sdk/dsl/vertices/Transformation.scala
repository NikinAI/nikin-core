package ai.nikin.pipeline.sdk.dsl.vertices

import ai.nikin.pipeline.sdk.dsl.vertices.Aggregation.AggregationFunction
import ai.nikin.typedgraph.core.Vertex

abstract class Transformation[_IN, _OUT](n: String) extends Vertex[Transformation[_IN, _OUT]](n) {
  override type IN  = _IN
  override type OUT = _OUT
}

case class Aggregation[_IN, _OUT](n: String, fn: AggregationFunction)
    extends Transformation[_IN, _OUT](n)

object Aggregation {
  sealed trait AggregationFunction {
    def col: String
  }
  case class Avg(col: String) extends AggregationFunction
  case class Max(col: String) extends AggregationFunction
  case class Min(col: String) extends AggregationFunction
  case class Sum(col: String) extends AggregationFunction
  case class Count(col: String) extends AggregationFunction
}
