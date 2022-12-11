package ai.nikin.pipeline

import ai.nikin.pipeline.sdk.Aggregation.AggregationFunction
import ai.nikin.typedgraph.core.{CanMakeEdge, Vertex}
import scala.annotation.{compileTimeOnly, StaticAnnotation}
import scala.language.experimental.macros
import scala.reflect.macros.whitebox
import zio.schema.{Schema => ZSchema}

package object sdk {
  type F_>>>[
      FROM <: Vertex[FROM],
      TO <: Vertex[TO] { type IN = FROM#OUT }
  ] = Flow[FROM, TO]

  implicit def transformToLake[DATA <: Product, IN]: CanMakeEdge[Transformation[IN, DATA], Flow, Lake[DATA]] =
    CanMakeEdge[Transformation[IN, DATA], Flow, Lake[DATA]](Flow)

  implicit def lakeToTransform[DATA <: Product, OUT]: CanMakeEdge[Lake[DATA], Flow, Transformation[DATA, OUT]] =
    CanMakeEdge[Lake[DATA], Flow, Transformation[DATA, OUT]](Flow)

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
