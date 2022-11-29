package ai.nikin.pipeline.sdk

import ai.nikin.pipeline.sdk.Aggregation.AggregationFunction

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
