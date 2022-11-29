package ai.nikin.pipeline.sdk.dsl

import ai.nikin.pipeline.sdk.dsl.vertices.Aggregation.AggregationFunction

package object vertices {
  def aggregation[IN, OUT](name: String, f: AggregationFunction): Transformation[IN, OUT] =
    Aggregation(name, f)

  def lake[DATA](name: String): Lake[DATA] = Lake(name)
}
