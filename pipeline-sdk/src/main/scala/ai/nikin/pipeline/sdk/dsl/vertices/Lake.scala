package ai.nikin.pipeline.sdk.dsl.vertices

import ai.nikin.typedgraph.core.Vertex

case class Lake[DATA](n: String) extends Vertex[Lake[DATA]](s"lake-$n") {
  final override type IN  = DATA
  final override type OUT = DATA
}
