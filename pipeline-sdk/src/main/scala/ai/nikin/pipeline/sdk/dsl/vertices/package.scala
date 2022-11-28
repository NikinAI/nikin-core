package ai.nikin.pipeline.sdk.dsl

package object vertices {
  def transformation[IN, OUT](name: String): Transformation[IN, OUT] = Transformation(name)

  def lake[DATA](name: String): Lake[DATA] = Lake(name)
}
