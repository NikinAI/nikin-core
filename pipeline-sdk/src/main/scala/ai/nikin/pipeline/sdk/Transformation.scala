package ai.nikin.pipeline.sdk

import ai.nikin.pipeline.model.BaseTransformation

abstract class Transformation[_IN, _OUT](n: String)
    extends Vertex[Transformation[_IN, _OUT]](n)
    with BaseTransformation {
  final override type IN  = _IN
  final override type OUT = _OUT
}
