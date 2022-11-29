package ai.nikin.pipeline

import ai.nikin.pipeline.sdk.Aggregation.AggregationFunction
import ai.nikin.typedgraph.core.{CanBeConnected, IVertex}

package object sdk {
  type F_>>>[
      FROM <: IVertex[FROM],
      TO <: IVertex[TO] { type IN = FROM#OUT }
  ] = Flow[FROM, TO]

  implicit def transformToLake[DATA, IN]: CanBeConnected[Transformation[IN, DATA], Flow, Lake[DATA]] =
    CanBeConnected[Transformation[IN, DATA], Flow, Lake[DATA]](Flow)

  implicit def lakeToTransform[DATA, OUT]: CanBeConnected[Lake[DATA], Flow, Transformation[DATA, OUT]] =
    CanBeConnected[Lake[DATA], Flow, Transformation[DATA, OUT]](Flow)

  def aggregation[IN, OUT](name: String, f: AggregationFunction): Transformation[IN, OUT] =
    Aggregation(name, f)

  def lake[DATA](name: String): Lake[DATA] = Lake(name)
}
