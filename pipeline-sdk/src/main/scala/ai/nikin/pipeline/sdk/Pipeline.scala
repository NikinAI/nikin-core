package ai.nikin.pipeline.sdk

import ai.nikin.typedgraph.core.{Graph, Vertex, Path}

import scala.language.implicitConversions

trait Pipeline {
  type PipelineDef = Graph
  implicit def toGraph[FROM <: Vertex[FROM], TO <: Vertex[TO]](tg: Path[FROM, TO]): PipelineDef = tg.asGraph

  def definition: PipelineDef
}
