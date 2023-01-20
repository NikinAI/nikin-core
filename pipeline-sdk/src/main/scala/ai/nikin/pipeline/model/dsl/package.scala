package ai.nikin.pipeline.model

import ai.nikin.typedgraph.core.Vertex
import io.scalaland.chimney.dsl._

package object dsl {
  sealed trait UntypedVertex {
    def name: String
  }

  @DslModel
  case class Lake[DATA <: Product](name: String, tpe: String)(implicit
                                                                            s: zio.schema.Schema[DATA]
  ) extends Vertex[Lake[DATA]](s"lake-$name") {
    final override type IN = DATA
    final override type OUT = DATA

    lazy final val schema: zio.schema.Schema[DATA] = s

    def toUntyped: UntypedLake = this.transformInto[UntypedLake]
  }

//  implicit class LakeExt(lake: Lake[_]) {
//    def toUntyped: UntypedLake = ???
//  }

  sealed abstract class Transformation[_IN, _OUT](n: String)
    extends Vertex[Transformation[_IN, _OUT]](n) {
    final override type IN = _IN
    final override type OUT = _OUT

    def inputTpe: String
    def outputTpe: String
  }

  @DslModel
  case class Aggregation[_IN, _OUT](
                                     name: String,
                                     aggFunction: AggregationFunction,
                                     inputTpe: String,
                                     outputTpe: String
                                   ) extends Transformation[_IN, _OUT](s"aggregation-$name") {
    def toUntyped: UntypedAggregation = this.transformInto[UntypedAggregation]
  }

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

}
