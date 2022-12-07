package ai.nikin.pipeline.sdk

import ai.nikin.typedgraph.core.{CanMakeEdge, Edge, EdgeFactory, Vertex, VertexTO}

import scala.annotation.unused

class Flow[
    FROM <: Vertex[FROM],
    TO <: VertexTO[FROM, TO]
](
    override val from: FROM,
    override val to:   TO
)(implicit
    @unused ev:        CanMakeEdge[FROM, Flow, TO]
) extends Edge[FROM, Flow, TO](from, to)

object Flow extends EdgeFactory[Flow] {
  override def apply[
      FROM <: Vertex[FROM],
      TO <: VertexTO[FROM, TO]
  ](from: FROM, to: TO)(implicit
      ev: CanMakeEdge[FROM, Flow, TO]
  ): FROM F_>>> TO = new Flow(from, to)
}
