package ai.nikin.pipeline.interpreter

import zio.schema.Schema.{Lazy, Primitive, Transform}
import zio.schema.{Schema, StandardType}

object Interpreter {
  def genDDL[T](input: Schema[T]): String =
    input match {
      case r: Schema.Record[T] =>
        r.fields.toList.map(field => s"${field.name} ${schemaDDL(field.schema)}").mkString(", ")
    }

  private def schemaDDL[T](input: Schema[T]): String =
    input match {
      case Transform(schema, _, _, _, _) => schemaDDL(schema)
      case Lazy(schema0)                 => schemaDDL(schema0())
      case r: Schema.Record[T] => recordStructDDL(r)
      case Primitive(standardType, _)          => primitiveDDL(standardType)
      case Schema.Sequence(schema, _, _, _, _) => arrayDDL(schema)
      case Schema.Set(schema, _)               => arrayDDL(schema)
      case Schema.Map(kSchema, vSchema, _)     => mapDDL(kSchema, vSchema)
      case Schema.Tuple2(leSchem, rSchema, _)  => tupleDDL(leSchem, rSchema)
      case Schema.Optional(schema, _)          => schemaDDL(schema)

      case _ => throw new Exception(s"Can't generate DDL for provided schema ${input.toString}")

    }

  private def tupleDDL[T1, T2](lSchema: Schema[T1], rSchema: Schema[T2]): String =
    s"STRUCT<_1 :${schemaDDL(lSchema)}, _2: ${schemaDDL(rSchema)}>"

  private def mapDDL[K, V](kSchema: Schema[K], vSchema: Schema[V]): String =
    s"MAP<${schemaDDL(kSchema)}, ${schemaDDL(vSchema)}>"

  private def recordStructDDL[T](schema: Schema.Record[T]): String =
    schema
      .fields
      .toList
      .map(field => s"${field.name}: ${schemaDDL(field.schema)}")
      .mkString("STRUCT<", ",", ">")

  private def arrayDDL[T](elemSchema: Schema[T]): String = s"ARRAY<${schemaDDL(elemSchema)}"

  private def primitiveDDL[T](standardType: StandardType[T]): String =
    standardType match {
      case StandardType.StringType     => "STRING"
      case StandardType.BoolType       => "BOOLEAN NOT NULL"
      case StandardType.ByteType       => "TINYINT NOT NULL"
      case StandardType.ShortType      => "SMALLINT NOT NULL"
      case StandardType.IntType        => "INT NOT NULL"
      case StandardType.LongType       => "BIGINT NOT NULL"
      case StandardType.FloatType      => "FLOAT NOT NULL"
      case StandardType.DoubleType     => "DOUBLE NOT NULL"
      case StandardType.BigIntegerType => "DECIMAL(38, 0)"
      case StandardType.BigDecimalType => "DECIMAL(38, 18)"
    }
}
