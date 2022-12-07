package ai.nikin.pipeline.sdk

import munit.Location

trait TestUtils extends munit.FunSuite {
  def testContained(compileError: String, expected: String*)(implicit loc: Location): Unit =
    expected.foreach(ex => assert(compileError.contains(ex), compileError))
}
