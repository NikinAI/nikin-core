package ai.nikin.pipeline

import ai.nikin.pipeline.sdk.Aggregation.AggregationFunction

package object model {
  trait BaseVertex {
    def name: String
  }

  trait BaseLake extends BaseVertex {
    def tpe: String
  }

  trait BaseTransformation extends BaseVertex {
    def inputTpe:  String
    def outputTpe: String
  }

  trait BaseAggregation extends BaseTransformation {
    def aggFunction: AggregationFunction
  }

  object Typeless {
    sealed trait Vertex extends BaseVertex
    case class Lake(name: String, tpe: String) extends BaseLake with Vertex

    case class Aggregation(
        name:        String,
        aggFunction: AggregationFunction,
        inputTpe:    String,
        outputTpe:   String
    ) extends BaseAggregation
        with Vertex

    import scalax.collection.edges.{DiEdge, DiEdgeImplicits}
    import scalax.collection.immutable.{Graph, TypedGraphFactory}

    def addVertex[V <: Vertex](v: V, graph: PipelineDef): PipelineDef = graph + v

    def removeVertex[V <: Vertex](v: V, graph: PipelineDef): PipelineDef = graph - v

    def addEdge[V1 <: Vertex, V2 <: Vertex](v1: V1, v2: V2, graph: PipelineDef): PipelineDef =
      (v1, v2) match {
        case (l: Lake, agg: Aggregation) if l.tpe == agg.inputTpe  => graph + (l ~> agg)
        case (agg: Aggregation, l: Lake) if agg.outputTpe == l.tpe => graph + (agg ~> l)
        case _                                                     => throw new RuntimeException("...")
      }

    def removeEdge[V1 <: Vertex, V2 <: Vertex](v1: V1, v2: V2, graph: PipelineDef): PipelineDef =
      graph - (v1 ~> v2)

    type PipelineDef = Graph[Vertex, DiEdge[Vertex]]

    object PipelineDef extends TypedGraphFactory[Vertex, DiEdge[Vertex]]

  }

}
