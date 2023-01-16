package ai.nikin.pipeline
package model

import io.scalaland.chimney.dsl._
import shapeless.{::, HList, HNil}

package object dsl {

  sealed abstract class Vertex[SELF <: Vertex[SELF]](val name: String) {
    type IN
    type OUT
  }

  sealed trait UntypedVertex {
    def name: String
  }

  @DslModel
  case class Lake[DATA <: Product](override val name: String, tpe: String)(implicit
      s: zio.schema.Schema[DATA]
  ) extends Vertex[Lake[DATA]](s"lake-$name") {
    final override type IN  = DATA
    final override type OUT = DATA

    lazy final val schema: zio.schema.Schema[DATA] = s

    def &[T <: Product](l: Lake[T]): CombinedLake[T :: DATA :: HNil] =
      CombinedLake[T :: DATA :: HNil](Set(this, l))

    def toUntyped: UntypedLake = this.transformInto[UntypedLake]
  }

  case class CombinedLake[L <: HList](lakes: Set[Lake[_]])
      extends Vertex[CombinedLake[L]](s"combined-${lakes.map(_.name).mkString("&")}") {
    final override type IN  = L
    final override type OUT = L

    def &[T <: Product](l: Lake[T]): CombinedLake[T :: L] = CombinedLake[T :: L](lakes + l)
  }

  sealed abstract class Transformation[_IN, _OUT](n: String)
      extends Vertex[Transformation[_IN, _OUT]](n) {
    final override type IN  = _IN
    final override type OUT = _OUT

    def inputTpe:  String
    def outputTpe: String
  }

  @DslModel
  case class Aggregation[_IN, _OUT](
      override val name: String,
      aggFunction:       sdk.AggregationFunction,
      inputTpe:          String,
      outputTpe:         String
  ) extends Transformation[_IN, _OUT](s"aggregation-$name") {
    def toUntyped: UntypedAggregation = this.transformInto[UntypedAggregation]
  }

}
