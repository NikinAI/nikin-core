package ai.nikin.pipeline.interpreter

import zio.schema.{DeriveSchema, Schema}

import scala.reflect.ClassTag

object Schemas {
  implicit def seqSchema[T](implicit schema: Schema[T]): Schema[Seq[T]] =
    Schema.Sequence[Seq[T], T, T => T](
      schema,
      _.toSeq,
      zio.Chunk.fromIterable(_),
      identity = identity(_)
    )

  implicit def setSchema[T](implicit schema: Schema[T]): Schema[Set[T]] =
    Schema.Sequence[Set[T], T, T => T](
      schema,
      _.toSet,
      zio.Chunk.fromIterable(_),
      identity = identity(_)
    )

  implicit def arraySchema[T](implicit schema: Schema[T], ct: ClassTag[T]): Schema[Array[T]] =
    Schema.Sequence[Array[T], T, T => T](
      schema,
      _.toArray,
      zio.Chunk.fromIterable(_),
      identity = identity(_)
    )

  implicit def listSchema[T](implicit schema: Schema[T]): Schema[List[T]] =
    Schema.Sequence[List[T], T, T => T](
      schema,
      _.toList,
      zio.Chunk.fromIterable(_),
      identity = identity(_)
    )
}
