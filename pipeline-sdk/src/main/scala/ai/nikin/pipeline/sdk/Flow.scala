package ai.nikin.pipeline.sdk

import ai.nikin.typedgraph.core._

import scala.annotation.unused

case class Flow[
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
