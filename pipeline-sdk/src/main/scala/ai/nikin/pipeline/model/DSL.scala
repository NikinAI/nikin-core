package ai.nikin.pipeline.model

object DSL {

  import scalax.collection.edges.{DiEdge, DiEdgeImplicits}
  import scalax.collection.immutable.{Graph, TypedGraphFactory}

  type PipelineDef = Graph[Vertex[_], DiEdge[Vertex[_]]]

  object PipelineDef extends TypedGraphFactory[Vertex[_], DiEdge[Vertex[_]]]

  type VertexTO[FROM <: Vertex[FROM], TO <: Vertex[TO] { type IN = FROM#OUT }] =
    Vertex[TO] { type IN = FROM#OUT }

  sealed abstract class Vertex[SELF <: Vertex[SELF]](val name: String) {
    type IN
    type OUT

    private var graph: PipelineDef = PipelineDef.empty

    def toGraph: PipelineDef = graph

    private[DSL] def addEdge[V <: Vertex[V]](next: V): V = {
      next.graph = this.graph + this ~> next
      next
    }

    def >>>[
        V <: VertexTO[SELF, V]
    ](next: V)(implicit ev: CanMakeEdge[SELF, V]): V = {
      println(ev) // TODO find a way to make CanConnect a context bound
      addEdge(next)
    }
  }

  sealed trait UntypedVertex {
    def name: String
  }

  @DslModel
  case class Lake[DATA <: Product](override val name: String, tpe: String)(implicit
      s:                                              zio.schema.Schema[DATA]
  ) extends Vertex[Lake[DATA]](name) {
    final override type IN  = DATA
    final override type OUT = DATA

    lazy final val schema: zio.schema.Schema[DATA] = s
  }

  sealed abstract class Transformation[_IN, _OUT](n: String)
      extends Vertex[Transformation[_IN, _OUT]](n) {
    final override type IN  = _IN
    final override type OUT = _OUT
  }

  @DslModel
  case class Aggregation[_IN, _OUT](
      override val name: String,
      aggFunction:       AggregationFunction,
      inputTpe:          String,
      outputTpe:         String
  ) extends Transformation[_IN, _OUT](s"aggregation-$name")

  sealed trait AggregationFunction {
    def inputColumn: String

    def outputColumn: String
  }

  object AggregationFunction {
    case class Avg(inputColumn: String, outputColumn: String) extends AggregationFunction

    case class Max(inputColumn: String, outputColumn: String) extends AggregationFunction

    case class Min(inputColumn: String, outputColumn: String) extends AggregationFunction

    case class Sum(inputColumn: String, outputColumn: String) extends AggregationFunction

    case class Count(inputColumn: String, outputColumn: String) extends AggregationFunction
  }

  import scala.annotation.implicitNotFound

  @implicitNotFound("""Connecting
    ${FROM}
to
    ${TO}
is not allowed with your current setup!

To enable this connectivity, add:

----
    implicit val ev = CanMakeEdge[${FROM}, ${EDGE}, ${TO}]()
----

In the scope of:
  """)
  case class CanMakeEdge[
      FROM <: Vertex[FROM],
      TO <: VertexTO[FROM, TO]
  ]()
}
