package ai.nikin.pipeline.interpreter

import ai.nikin.pipeline.interpreter.ddl.DDLMapper
import zio.schema.Schema

object DeltaLakeDDLGenerator {
  def generateDDL[T <: Product](tableName: String)(implicit schema: Schema[T]): String = {
    val init = "CREATE TABLE IF NOT EXISTS"

    val ddl      = DDLMapper.mapToDDLType(schema).toDDL
    val location =
      s"""USING DELTA
        |    LOCATION s3a://BUCKET_PLACEHOLDER/$tableName""".stripMargin

    s"""$init $tableName(
      |$ddl
      |) $location""".stripMargin
  }
}
