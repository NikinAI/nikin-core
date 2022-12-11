package ai.nikin.pipeline.sdk

import ai.nikin.typedgraph.core.Vertex

case class Lake[DATA <: Product](name: String)(implicit s: zio.schema.Schema[DATA])
    extends Vertex[Lake[DATA]](s"lake-$name") {
  final override type IN  = DATA
  final override type OUT = DATA

  lazy final val schema: zio.schema.Schema[DATA] = s
}
