package ai.nikin.pipeline.sdk

import ai.nikin.pipeline.sdk.Aggregation.AggregationFunction

case class Aggregation[_IN, _OUT](n: String, fn: AggregationFunction)
    extends Transformation[_IN, _OUT](n)

object Aggregation {
  sealed trait AggregationFunction {
    def inputColumn:  String
    def outputColumn: String
  }
  case class Avg(inputColumn: String, outputColumn: String) extends AggregationFunction
  case class Max(inputColumn: String, outputColumn: String) extends AggregationFunction
  case class Min(inputColumn: String, outputColumn: String) extends AggregationFunction
  case class Sum(inputColumn: String, outputColumn: String) extends AggregationFunction
  case class Count(inputColumn: String, outputColumn: String) extends AggregationFunction
}
