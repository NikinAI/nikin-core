package ai.nikin.pipeline.sdk

import ai.nikin.typedgraph.core.Vertex

case class Lake[DATA](name: String) extends Vertex[Lake[DATA]](s"lake-$name") {
  final override type IN  = DATA
  final override type OUT = DATA
}
