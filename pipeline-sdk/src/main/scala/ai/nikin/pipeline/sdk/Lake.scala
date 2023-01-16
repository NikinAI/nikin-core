package ai.nikin.pipeline.sdk

case class Lake[DATA <: Product](override val name: String)(implicit s: zio.schema.Schema[DATA])
    extends Vertex[Lake[DATA]](name) {
  final override type IN  = DATA
  final override type OUT = DATA

  lazy final val schema: zio.schema.Schema[DATA] = s
}
