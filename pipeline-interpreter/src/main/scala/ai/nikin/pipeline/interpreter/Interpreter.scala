package ai.nikin.pipeline.interpreter

import zio.schema.Schema

object Interpreter {
  def mapToDDL[T <: Product](implicit schema: Schema[T]): String =
    DDLMapper.mapToDDLType(schema).toDDL
}
