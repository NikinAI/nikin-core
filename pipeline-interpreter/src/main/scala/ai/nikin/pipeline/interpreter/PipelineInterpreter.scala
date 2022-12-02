package ai.nikin.pipeline.interpreter

import ai.nikin.pipeline.interpreter.Artifact.{AggregationArtifact, LakeArtifact}
import ai.nikin.pipeline.sdk.{Aggregation, Lake}
import ai.nikin.typedgraph.core.Vertex.AnyVertex
import ai.nikin.typedgraph.core.Graph

object PipelineInterpreter {
  case class Test()

  def process(pipeline: Graph): Map[String, Artifact] = {
    //
    val lakes =
      pipeline.foldVertexLeft(Map.empty[String, Artifact]) { (acc, vertex) =>
        processVertex(acc)(vertex)
      }

    lakes
  }

  private def processVertex(acc: Map[String, Artifact]): AnyVertex => Map[String, Artifact] = {
    case Lake(n) if acc.contains(n)                 => acc
    case l @ Lake(_)                                =>
      val ddl = DeltaLakeDDLGenerator.generateDDL(l.schema)
      acc + (l.name -> LakeArtifact(ddl))
    case Aggregation(name, _) if acc.contains(name) => acc
    case a @ Aggregation(_, _)                      => acc + (a.name -> AggregationArtifact())
    case other                                      => throw new Exception(s"Don't know how to handle '$other''")
  }
}
