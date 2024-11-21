package ai.nikin.deployment.spark

import zio.json._

import java.time.Duration

package object model {

  @jsonDerive sealed trait TransformationType
  case object SimpleTransformation extends TransformationType
  case object FanIn2Transformation extends TransformationType
  case object FanIn3Transformation extends TransformationType
  case object FanIn4Transformation extends TransformationType
  
  sealed trait JobDescription {
    def name: String
    def mainClass: String
    def transformationType: TransformationType
  }

  @jsonDerive case class BatchJobDescription(name: String, mainClass: String, transformationType: TransformationType, sources: Seq[BatchSource], sinks: Seq[BatchSink]) extends JobDescription
  @jsonDerive case class StreamingJobDescription(name: String, mainClass: String, transformationType: TransformationType, sources: Seq[StreamingSourceConnector], sinks: Seq[StreamingSink], checkpointLocation: Option[String] = None) extends JobDescription
  sealed trait FusedJobDescription
  @jsonDerive case class FusedBatchJobDescription(name: String, jobs: Seq[BatchJobDescription]) extends FusedJobDescription
  @jsonDerive case class FusedStreamingJobDescription(name: String, jobs: Seq[StreamingJobDescription], checkpointLocation: String) extends FusedJobDescription

  @jsonDerive case class Watermark(column: String, delay: Duration, castToTimestamp: Boolean = false)

  sealed trait Connector {
    def name: String
    def location: String
  }
  sealed trait BatchConnector extends Connector
  @jsonDerive case class BatchSource(name: String, location: String, materialized: Boolean) extends BatchConnector
  @jsonDerive case class BatchSink(name: String, location: String, materialized: Boolean) extends BatchConnector

  sealed trait StreamingConnector extends Connector
  @jsonDerive sealed trait StreamingSourceConnector extends StreamingConnector
  case class StreamingSource(name: String, location: String, materialized: Boolean, watermark: Option[Watermark] = None) extends StreamingSourceConnector
  //case class FusedSource(name: String, location: String) extends StreamingSourceConnector
  case class LookupSource(name: String, location: String) extends StreamingSourceConnector
  @jsonDerive case class StreamingSink(name: String, location: String, materialized: Boolean, trigger: Option[Duration] = None) extends StreamingConnector
  //case class FusedSink(name: String, location: String) extends StreamingConnector
}
