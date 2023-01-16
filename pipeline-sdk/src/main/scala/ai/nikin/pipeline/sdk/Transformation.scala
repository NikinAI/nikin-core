package ai.nikin.pipeline.sdk

abstract class Transformation[_IN, _OUT](n: String) extends Vertex[Transformation[_IN, _OUT]](n) {
  final override type IN  = _IN
  final override type OUT = _OUT
}
