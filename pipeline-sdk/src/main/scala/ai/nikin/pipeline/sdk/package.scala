package ai.nikin.pipeline

import ai.nikin.pipeline.model.dsl._
import scala.annotation.{compileTimeOnly, unused, StaticAnnotation}
import scala.language.experimental.macros
import scala.language.implicitConversions
import scala.reflect.runtime.universe._
import scala.reflect.macros.whitebox
import zio.schema.{Schema => ZSchema}

package object sdk {

  import scalax.collection.edges.{DiEdge, DiEdgeImplicits}
  import scalax.collection.immutable.{Graph, TypedGraphFactory}

  type PipelineDef = Graph[Vertex[_], DiEdge[Vertex[_]]]

  object PipelineDef extends TypedGraphFactory[Vertex[_], DiEdge[Vertex[_]]]

  implicit def toGraph[V <: Vertex[V]](v: PipelineBuilder[V]): PipelineDef = v.graph

  implicit def toPipelineStep[V <: Vertex[V]](v: V): PipelineBuilder[V] = PipelineBuilder(v, PipelineDef.empty)

  case class PipelineBuilder[SELF <: Vertex[SELF]](v: SELF, graph: PipelineDef) {
    def >>>[
      V <: VertexTO[SELF, V]
    ](next: V)(implicit @unused ev: CanMakeEdge[SELF, V]): PipelineBuilder[V] = {
      PipelineBuilder(next, graph + v ~> next)
    }
  }

  abstract class Vertex[SELF <: Vertex[SELF]](val name: String) {
    type IN
    type OUT
  }

  type VertexTO[FROM <: Vertex[FROM], TO <: Vertex[TO] { type IN = FROM#OUT }] =
    Vertex[TO] { type IN = FROM#OUT }

  implicit def transformToLake[DATA <: Product, IN]: CanMakeEdge[Transformation[IN, DATA], Lake[DATA]] =
    CanMakeEdge[Transformation[IN, DATA], Lake[DATA]]()

  implicit def lakeToTransform[DATA <: Product, OUT]: CanMakeEdge[Lake[DATA], Transformation[DATA, OUT]] =
    CanMakeEdge[Lake[DATA], Transformation[DATA, OUT]]()

  def aggregation[IN <: Product, OUT <: Product](name: String, f: AggregationFunction)(implicit
      inTypeTag:  WeakTypeTag[IN],
      outTypeTag: WeakTypeTag[OUT]
  ): Transformation[IN, OUT] = Aggregation(name, f, extractFQN(inTypeTag), extractFQN(outTypeTag))

  def lake[DATA <: Product : ZSchema](name: String)(implicit
      typeTag: WeakTypeTag[DATA]
  ): Lake[DATA] = Lake(name, extractFQN(typeTag))

  private[sdk] def extractFQN[T](typeTag: WeakTypeTag[T]): String = typeTag.tpe.typeSymbol.fullName

  @compileTimeOnly("enable macro paradise")
  class Schema extends StaticAnnotation {
    def macroTransform(annottees: Any*): Any = macro SchemaMacro.impl
  }

  object SchemaMacro {
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
