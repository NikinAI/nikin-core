package ai.nikin.pipeline.sdk

import scala.language.implicitConversions

trait Pipeline {

  implicit def toGraph[V <: Vertex[V]](v: V): PipelineDef = v.graph

  def definition: PipelineDef
}
