package ai.nikin.pipeline.interpreter

import org.apache.spark.sql.Encoders

object Interpreter {
  def genDDLWithSpark[T <: Product](input: T): String = Encoders.product[T].schema.toDDL
}
