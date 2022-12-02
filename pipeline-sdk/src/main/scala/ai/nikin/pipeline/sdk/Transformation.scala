package ai.nikin.pipeline.sdk

import ai.nikin.typedgraph.core.Vertex

abstract class Transformation[_IN , _OUT ](n: String)
    extends Vertex[Transformation[_IN, _OUT]](n) {
  override type IN  = _IN
  override type OUT = _OUT
}
