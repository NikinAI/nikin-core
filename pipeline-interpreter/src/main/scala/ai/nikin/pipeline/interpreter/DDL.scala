package ai.nikin.pipeline.interpreter

object DDL {
  sealed trait DDLType {
    def toDDL: String
  }
  sealed trait DDLContainer extends DDLType
  sealed trait NullableDDLType extends DDLType {
    val isNullable: Boolean
  }

  case class StringType(override val isNullable: Boolean) extends NullableDDLType {
    override def toDDL: String =
      isNullable match {
        case false => s"STRING NOT NULL"
        case true  => s"STRING"
      }
  }

  case class BoolType(override val isNullable: Boolean) extends NullableDDLType {
    override def toDDL: String =
      isNullable match {
        case false => "BOOLEAN NOT NULL"
        case true  => "BOOLEAN"
      }
  }

  case class ByteType(override val isNullable: Boolean) extends NullableDDLType {
    override def toDDL: String =
      isNullable match {
        case false => "TINYINT NOT NULL"
        case true  => "TINYINT"
      }
  }

  case class ShortType(override val isNullable: Boolean) extends NullableDDLType {
    override def toDDL: String =
      isNullable match {
        case false => "SMALLINT NOT NULL"
        case true  => "SMALLINT"
      }
  }

  case class IntType(override val isNullable: Boolean) extends NullableDDLType {
    override def toDDL: String =
      isNullable match {
        case false => "INT NOT NULL"
        case true  => "INT"
      }
  }

  case class LongType(override val isNullable: Boolean) extends NullableDDLType {
    override def toDDL: String =
      isNullable match {
        case false => "BIGINT NOT NULL"
        case true  => "BIGINT"
      }
  }

  case class FloatType(override val isNullable: Boolean) extends NullableDDLType {
    override def toDDL: String =
      isNullable match {
        case false => "FLOAT NOT NULL"
        case true  => "FLOAT"
      }
  }

  case class DoubleType(override val isNullable: Boolean) extends NullableDDLType {
    override def toDDL: String =
      isNullable match {
        case false => "DOUBLE NOT NULL"
        case true  => "DOUBLE"
      }
  }

  case object BigIntegerType extends DDLType {
    override def toDDL: String = "DECIMAL(38,0)"
  }

  case object BigDecimalType extends DDLType {
    override def toDDL: String = "DECIMAL(38,18)"
  }

  case class MapContainer(key: DDLType, value: DDLType) extends DDLContainer {
    override def toDDL: String = s"MAP<${key.toDDL}, ${value.toDDL}>"
  }

  case class ArrayContainer(value: DDLType) extends DDLContainer {
    override def toDDL: String =
      value match {
        case ByteType(_) => "BINARY"
        case _           => s"ARRAY<${value.toDDL}>"
      }
  }

  case class MainContainer(values: List[(String, DDLType)]) extends DDLContainer {
    override def toDDL: String =
      values.map { case (name, ddl) => s"$name ${ddl.toDDL}" }.mkString(",")
  }

  case class StructContainer(values: List[(String, DDLType)]) extends DDLContainer {
    override def toDDL: String =
      values.map { case (name, ddl) => s"$name: ${ddl.toDDL}" }.mkString("STRUCT<", ", ", ">")
  }

  case class TupleContainer(values: List[DDLType]) extends DDLContainer {
    override def toDDL: String =
      flatten()
        .zipWithIndex
        .map { case (elem, idx) => s"_${idx + 1}: ${elem.toDDL}" }
        .mkString("STRUCT<", ", ", ">")

    def flatten() = {
      def recursive(flattened: List[DDLType], values: List[DDLType]): List[DDLType] =
        values match {
          case Nil                               => flattened
          case TupleContainer(tupleVals) :: tail => recursive(flattened, tupleVals ++ tail)
          case value :: tail                     => recursive(flattened :+ value, tail)
        }

      recursive(List(), values)
    }
  }
}
