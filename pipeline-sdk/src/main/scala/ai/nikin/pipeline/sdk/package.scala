package ai.nikin.pipeline

import ai.nikin.pipeline.sdk.Aggregation.AggregationFunction

import scala.annotation.{compileTimeOnly, StaticAnnotation}
import scala.language.experimental.macros
import scala.reflect.macros.whitebox
import zio.schema.{Schema => ZSchema}

package object sdk {

  import scalax.collection.edges.{DiEdge, DiEdgeImplicits}
  import scalax.collection.immutable.{Graph, TypedGraphFactory}

  type PipelineDef = Graph[Vertex[_], DiEdge[Vertex[_]]]

  object PipelineDef extends TypedGraphFactory[Vertex[_], DiEdge[Vertex[_]]]

  abstract class Vertex[SELF <: Vertex[SELF]](val name: String) extends Product {
    type IN
    type OUT

    private[sdk] var graph: PipelineDef = PipelineDef.empty

    private[sdk] def addEdge[V <: Vertex[V]](next: V): V = {
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

  type VertexTO[FROM <: Vertex[FROM], TO <: Vertex[TO] { type IN = FROM#OUT }] =
    Vertex[TO] { type IN = FROM#OUT }

  implicit def transformToLake[DATA <: Product, IN]: CanMakeEdge[Transformation[IN, DATA], Lake[DATA]] =
    CanMakeEdge[Transformation[IN, DATA], Lake[DATA]]()

  implicit def lakeToTransform[DATA <: Product, OUT]: CanMakeEdge[Lake[DATA], Transformation[DATA, OUT]] =
    CanMakeEdge[Lake[DATA], Transformation[DATA, OUT]]()

  def aggregation[IN, OUT](name: String, f: AggregationFunction): Transformation[IN, OUT] =
    Aggregation(name, f)

  def lake[DATA <: Product](name: String)(implicit schema: ZSchema[DATA]): Lake[DATA] =
    Lake(name)(schema)

  @compileTimeOnly("enable macro paradise")
  class Schema extends StaticAnnotation {
    def macroTransform(annottees: Any*): Any = macro Macro.impl
  }

  object Macro {
    def impl(c: whitebox.Context)(annottees: c.Tree*): c.Tree = {
      import c.universe._
      annottees match {
        case (cls @ q"$_ class $tpname[..$_] $_(...$_) extends { ..$_ } with ..$_ { $_ => ..$_ }") ::
             Nil => q"""
             $cls

             object ${TermName(tpname.toString)} {
               import zio.schema.{DeriveSchema, Schema}
               implicit val schema: Schema[$tpname] = DeriveSchema.gen[$tpname]
             }
           """
      }
    }
  }
}
