package ai.nikin.pipeline.sdk

import ai.nikin.typedgraph.core.{Graph, IVertex, Path}

import scala.language.implicitConversions

trait Pipeline {
  type PipelineDef = Graph
  implicit def toGraph[FROM <: IVertex[FROM], TO <: IVertex[TO]](tg: Path[FROM, TO]): PipelineDef = tg.inGraph

  def definition: PipelineDef
}
