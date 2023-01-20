package ai.nikin.pipeline.sdk

sealed trait AggregationFunction {
  def inputColumn: String

  def outputColumn: String
}

object AggregationFunction {
  case class Avg(inputColumn: String, outputColumn: String) extends AggregationFunction

  case class Max(inputColumn: String, outputColumn: String) extends AggregationFunction

  case class Min(inputColumn: String, outputColumn: String) extends AggregationFunction

  case class Sum(inputColumn: String, outputColumn: String) extends AggregationFunction

  case class Count(inputColumn: String, outputColumn: String) extends AggregationFunction
}
