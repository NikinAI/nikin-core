package ai.nikin.pipeline

import model.DSL._

package object runtime {

  import scalax.collection.edges.{DiEdge, DiEdgeImplicits}
  import scalax.collection.immutable.{Graph, TypedGraphFactory}

  // TODO these are just ideas for an API

  def addVertex[V <: UntypedVertex](v: V, graph: PipelineDef): PipelineDef = graph + v

  def removeVertex[V <: UntypedVertex](v: V, graph: PipelineDef): PipelineDef = graph - v

  def addEdge[V1 <: UntypedVertex, V2 <: UntypedVertex](
      v1:    V1,
      v2:    V2,
      graph: PipelineDef
  ): PipelineDef =
    (v1, v2) match {
      case (l: UntypedLake, agg: UntypedAggregation) if l.tpe == agg.inputTpe  => graph + (l ~> agg)
      case (agg: UntypedAggregation, l: UntypedLake) if agg.outputTpe == l.tpe => graph + (agg ~> l)
      case _                                                                   => throw new RuntimeException("...")
    }

  def removeEdge[V1 <: UntypedVertex, V2 <: UntypedVertex](
      v1:    V1,
      v2:    V2,
      graph: PipelineDef
  ): PipelineDef = graph - (v1 ~> v2)

  type PipelineDef = Graph[UntypedVertex, DiEdge[UntypedVertex]]

  object PipelineDef extends TypedGraphFactory[UntypedVertex, DiEdge[UntypedVertex]]

}
