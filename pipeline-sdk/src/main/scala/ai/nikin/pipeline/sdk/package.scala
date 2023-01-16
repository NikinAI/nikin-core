package ai.nikin.pipeline

import ai.nikin.pipeline.model.dsl._
import scala.annotation.{compileTimeOnly, unused, StaticAnnotation}
import scala.language.experimental.macros
import scala.language.implicitConversions
import scala.reflect.runtime.universe._
import scala.reflect.macros.whitebox
import zio.schema.{Schema => ZSchema}

package object sdk {

  import scalax.collection.GraphEdge.DiEdge
  import scalax.collection.immutable.Graph
  import scalax.collection.GraphPredef._
  import shapeless.HList

  type PipelineDef = Graph[Vertex[_], DiEdge]

  implicit def toGraph[V <: Vertex[V]](pb: PipelineBuilder[V]): PipelineDef = pb.graph

  implicit def toPipelineBuilder[V <: Vertex[V]](v: V): PipelineBuilder[V] =
    new PipelineBuilder(v, Graph.empty[Vertex[_], DiEdge])

  class PipelineBuilder[SELF <: Vertex[SELF]] private[sdk] (
      private[sdk] val v:     SELF,
      private[sdk] val graph: PipelineDef
  ) {
    def >>>[
        V <: VertexTO[SELF, V]
    ](next: V)(implicit @unused ev: CanMakeEdge[SELF, V]): PipelineBuilder[V] =
      v match {
        case cb: CombinedLake[_] => new PipelineBuilder(next, graph ++ cb.lakes.map(_ ~> next))
        case _ => new PipelineBuilder(next, graph ++ Set(v ~> next))
      }
  }

  type VertexTO[FROM <: Vertex[FROM], TO <: Vertex[TO] { type IN = FROM#OUT }] =
    Vertex[TO] { type IN = FROM#OUT }

  implicit def transformToLake[DATA <: Product, IN]: CanMakeEdge[Transformation[IN, DATA], Lake[DATA]] =
    CanMakeEdge[Transformation[IN, DATA], Lake[DATA]]()

  implicit def lakeToTransform[DATA <: Product, OUT]: CanMakeEdge[Lake[DATA], Transformation[DATA, OUT]] =
    CanMakeEdge[Lake[DATA], Transformation[DATA, OUT]]()

  implicit def combinedLakeToTransform[L <: HList, OUT]: CanMakeEdge[CombinedLake[L], Transformation[L, OUT]] =
    CanMakeEdge[CombinedLake[L], Transformation[L, OUT]]()

  implicit def transformToCombinedLake[DATA <: HList, IN]: CanMakeEdge[Transformation[IN, DATA], CombinedLake[DATA]] =
    CanMakeEdge[Transformation[IN, DATA], CombinedLake[DATA]]()

  def aggregation[IN <: Product, OUT <: Product](name: String, f: AggregationFunction)(implicit
      inTypeTag:  WeakTypeTag[IN],
      outTypeTag: WeakTypeTag[OUT]
  ): Transformation[IN, OUT] = Aggregation(name, f, extractFQN(inTypeTag), extractFQN(outTypeTag))

  def lake[DATA <: Product : ZSchema](name: String)(implicit
      typeTag: WeakTypeTag[DATA]
  ): Lake[DATA] = Lake(name, extractFQN(typeTag))

  private[sdk] def extractFQN[T](typeTag: WeakTypeTag[T]): String = typeTag.tpe.toString

  implicit def schemaGen[T]: ZSchema[T] = macro zio.schema.DeriveSchema.genImpl[T]

  // TODO We keep the Schema macro & annotation as a noop, because we expect it to become useful again in the near future
  @compileTimeOnly("enable macro paradise")
  class Schema extends StaticAnnotation {
    @unused
    def macroTransform(@unused annottees: Any*): Any = macro SchemaMacro.impl
  }

  object SchemaMacro {
    def impl(c: whitebox.Context)(annottees: c.Tree*): c.Tree = {
      import c.universe._
      annottees match {
        case (cls @ q"$_ class $_[..$_] $_(...$_) extends { ..$_ } with ..$_ { $_ => ..$_ }") ::
             Nil => q"""$cls"""
      }
    }
  }
}
