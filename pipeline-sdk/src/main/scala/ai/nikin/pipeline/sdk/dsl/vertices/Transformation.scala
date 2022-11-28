package ai.nikin.pipeline.sdk.dsl.vertices

import ai.nikin.typedgraph.core.Vertex

case class Transformation[_IN, _OUT](n: String)
    extends Vertex[Transformation[_IN, _OUT]](s"transformation-$n") {
  override type IN  = _IN
  override type OUT = _OUT
}
