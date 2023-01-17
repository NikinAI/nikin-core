package ai.nikin.pipeline.sdk

import ai.nikin.pipeline.model.BaseLake
import zio.schema.{Schema => ZSchema}

case class Lake[DATA <: Product](override val name: String, override val tpe: String)(implicit
    s:                                              ZSchema[DATA]
) extends Vertex[Lake[DATA]](name)
    with BaseLake {
  final override type IN  = DATA
  final override type OUT = DATA

  lazy final val schema: zio.schema.Schema[DATA] = s
}
