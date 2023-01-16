package ai.nikin.pipeline.sdk

import scala.annotation.implicitNotFound

@implicitNotFound("""Connecting
    ${FROM}
to
    ${TO}
is not allowed with your current setup!

To enable this connectivity, add:

----
    implicit val ev = CanMakeEdge[${FROM}, ${EDGE}, ${TO}]()
----

In the scope of:
  """)
case class CanMakeEdge[
    FROM <: Vertex[FROM],
    TO <: VertexTO[FROM, TO]
]()
