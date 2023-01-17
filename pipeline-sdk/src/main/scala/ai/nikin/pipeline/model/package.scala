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
    sealed trait Vertex
    case class Lake(name: String, tpe: String) extends BaseLake with Vertex

    case class Aggregation(
        name:        String,
        aggFunction: AggregationFunction,
        inputTpe:    String,
        outputTpe:   String
    ) extends BaseAggregation
        with Vertex

//    import scalax.collection.edges.{DiEdge, DiEdgeImplicits}
//    import scalax.collection.immutable.{Graph, TypedGraphFactory}
//
//    type PipelineDef = Graph[Vertex, DiEdge[Vertex]]
//
//    object PipelineDef extends TypedGraphFactory[Vertex, DiEdge[Vertex]]

  }

}
