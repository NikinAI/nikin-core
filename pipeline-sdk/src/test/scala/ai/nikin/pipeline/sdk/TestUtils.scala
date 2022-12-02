package ai.nikin.pipeline.sdk

object TestUtils {
  def testContained(compileError: String, expected: String*): Unit =
    expected.foreach(ex => assert(compileError.contains(ex), compileError))
}
