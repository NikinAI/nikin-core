package ai.nikin.pipeline.sdk

import ai.nikin.typedgraph.core.{CanBeConnected, Edge, EdgeDependency, IVertex}

import scala.annotation.unused

class Flow[
    FROM <: IVertex[FROM],
    TO <: IVertex[TO]
](
    override val from: FROM,
    override val to:   TO
)(implicit
    @unused ev:        CanBeConnected[FROM, Flow, TO]
) extends Edge[FROM, Flow, TO](from, to)

object Flow extends EdgeDependency[Flow] {
  override def apply[
      FROM <: IVertex[FROM],
      TO <: IVertex[TO] { type IN = FROM#OUT }
  ](from: FROM, to: TO)(implicit
      ev: CanBeConnected[FROM, Flow, TO]
  ): FROM F_>>> TO = new Flow(from, to)
}
