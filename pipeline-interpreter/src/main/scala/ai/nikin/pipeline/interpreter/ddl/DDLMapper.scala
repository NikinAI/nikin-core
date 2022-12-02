package ai.nikin.pipeline.interpreter.ddl

import ai.nikin.pipeline.interpreter.ddl.DDL._
import zio.schema.Schema.{Lazy, Primitive, Transform}
import zio.schema.{Schema, StandardType}

object DDLMapper {
  def mapToDDLType[T](input: Schema[T]): DDLType =
    input match {
      case r: Schema.Record[T] =>
        val mainContainerFields =
          r.fields.toList.map(field => (field.name, schemaDDL(field.schema)))

        MainContainer(mainContainerFields)
      case _ => throw new Exception("Only Case Classes are supported for DDL generation.")
    }

  private def schemaDDL[T](input: Schema[T], isNullable: Boolean = false): DDLType =
    input match {
      case Transform(schema, _, _, _, _) => schemaDDL(schema, isNullable)
      case Lazy(schema0)                 => schemaDDL(schema0(), isNullable)
      case r: Schema.Record[T] => recordStructDDL(r)
      case Primitive(standardType, _)          => primitiveDDL(standardType, isNullable)
      case Schema.Sequence(schema, _, _, _, _) => arrayDDL(schema)
      case Schema.Set(schema, _)               => arrayDDL(schema)
      case Schema.Map(kSchema, vSchema, _)     => mapDDL(kSchema, vSchema)
      case Schema.Tuple2(leSchem, rSchema, _)  => tupleDDL(leSchem, rSchema)
      case Schema.Optional(schema, _)          => schemaDDL(schema, true)

      case _ => throw new Exception(s"Can't generate DDL for provided schema ${input.toString}")
    }

  private def tupleDDL[T1, T2](lSchema: Schema[T1], rSchema: Schema[T2]): DDLType = {
    val schemas = List(lSchema, rSchema).map(schemaDDL(_, true))
    TupleContainer(schemas)
  }

  private def mapDDL[K, V](kSchema: Schema[K], vSchema: Schema[V]): DDLType =
    MapContainer(schemaDDL(kSchema, true), schemaDDL(vSchema, true))

  private def recordStructDDL[T](schema: Schema.Record[T]): DDLType = {
    val schemas = schema.fields.toList.map(field => (field.name, schemaDDL(field.schema, true)))

    StructContainer(schemas)
  }

  private def arrayDDL[T](elemSchema: Schema[T]): DDLType =
    ArrayContainer(schemaDDL(elemSchema, true))

  private def primitiveDDL[T](standardType: StandardType[T], isNullable: Boolean): DDLType =
    standardType match {
      case StandardType.StringType     => StringType(isNullable)
      case StandardType.BoolType       => BoolType(isNullable)
      case StandardType.ByteType       => ByteType(isNullable)
      case StandardType.ShortType      => ShortType(isNullable)
      case StandardType.IntType        => IntType(isNullable)
      case StandardType.LongType       => LongType(isNullable)
      case StandardType.FloatType      => FloatType(isNullable)
      case StandardType.DoubleType     => DoubleType(isNullable)
      case StandardType.BigIntegerType => BigIntegerType
      case StandardType.BigDecimalType => BigDecimalType
      case _                           => throw new Exception(s"Type $standardType is not supported!")
    }
}
