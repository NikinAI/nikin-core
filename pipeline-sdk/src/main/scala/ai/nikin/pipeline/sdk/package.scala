package ai.nikin.pipeline

import ai.nikin.pipeline.sdk.Aggregation.AggregationFunction
import ai.nikin.typedgraph.core.{CanMakeEdge, Vertex}

package object sdk {
  type F_>>>[
      FROM <: Vertex[FROM],
      TO <: Vertex[TO] { type IN = FROM#OUT }
  ] = Flow[FROM, TO]

  implicit def transformToLake[DATA, IN]: CanMakeEdge[Transformation[IN, DATA], Flow, Lake[DATA]] =
    CanMakeEdge[Transformation[IN, DATA], Flow, Lake[DATA]](Flow)

  implicit def lakeToTransform[DATA, OUT]: CanMakeEdge[Lake[DATA], Flow, Transformation[DATA, OUT]] =
    CanMakeEdge[Lake[DATA], Flow, Transformation[DATA, OUT]](Flow)

  def aggregation[IN, OUT](name: String, f: AggregationFunction): Transformation[IN, OUT] =
    Aggregation(name, f)

  def lake[DATA](name: String): Lake[DATA] = Lake(name)
}
