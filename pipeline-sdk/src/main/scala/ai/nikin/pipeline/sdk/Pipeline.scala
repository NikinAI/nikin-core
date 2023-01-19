package ai.nikin.pipeline.sdk

import ai.nikin.pipeline.model.DSL.PipelineDef

trait Pipeline {

  def definition: PipelineDef
}
