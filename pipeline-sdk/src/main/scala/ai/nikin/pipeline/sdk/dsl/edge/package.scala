package ai.nikin.pipeline.sdk.dsl

import ai.nikin.pipeline.sdk.dsl.vertices.{Lake, Transformation}
import ai.nikin.typedgraph.core.{CanBeConnected, IVertex}

package object edge {
  type F_>>>[
      FROM <: IVertex[FROM],
      TO <: IVertex[TO] { type IN = FROM#OUT }
  ] = Flow[FROM, TO]

  implicit def transformToLake[DATA, IN]: CanBeConnected[Transformation[IN, DATA], Flow, Lake[DATA]] =
    CanBeConnected[Transformation[IN, DATA], Flow, Lake[DATA]](Flow)

  implicit def lakeToTransform[DATA, OUT]: CanBeConnected[Lake[DATA], Flow, Transformation[DATA, OUT]] =
    CanBeConnected[Lake[DATA], Flow, Transformation[DATA, OUT]](Flow)
}
