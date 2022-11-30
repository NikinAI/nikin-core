package ai.nikin.pipeline.interpreter

import ai.nikin.pipeline.interpreter.ddl.DDLMapper
import zio.schema.Schema
import zio.schema.meta.MetaSchema

trait Interpreter {
  def generateDDL[T <: Product](implicit schema: Schema[T]): String
}

object DeltaLakeKeeperInterpreter extends Interpreter {
  override def generateDDL[T <: Product](implicit schema: Schema[T]): String = {
    val init     = "CREATE TABLE IF NOT EXISTS"
    val typeName =
      schema.ast match {
        case MetaSchema.Product(id, _, _, _) => id.name
        case _                               => throw new Exception("DDL generation is only supported for case classes.")
      }

    val ddl      = DDLMapper.mapToDDLType(schema).toDDL
    val location =
      s"""USING DELTA
        |    LOCATION s3a://BUCKET_PLACEHOLDER/$typeName""".stripMargin

    s"""$init $typeName(
      |$ddl
      |) $location""".stripMargin
  }
}
