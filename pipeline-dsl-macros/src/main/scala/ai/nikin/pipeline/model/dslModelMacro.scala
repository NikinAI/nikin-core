package ai.nikin.pipeline.model

import scala.annotation.{compileTimeOnly, StaticAnnotation}
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

@compileTimeOnly("enable macro paradise")
class DslModel extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro DslModelMacro.impl
}

object DslModelMacro {
  def impl(c: whitebox.Context)(annottees: c.Tree*): c.Tree = {
    import c.universe._
    annottees.head match {
      case ClassDef(mods, className, tparams, Template(parents, self, body)) =>
        // This is needed in order to workaround this bug related to implicit params and bounded contexts: https://github.com/scala/bug/issues/10589
        val newBody =
          body.map {
            case ValDef(mods, name, tpt, rhs) =>
              // the flag of `private[this]` is Flag.PRIVATE | Flag.LOCAL
              // the flag of `private` is Flag.PRIVATE
              // drop Flag.LOCAL in Modifiers.flags ,  it will change `private[this]` to `private`
              val newMods =
                if(mods.hasFlag(Flag.IMPLICIT)) mods
                  .asInstanceOf[scala.reflect.internal.Trees#Modifiers]
                  .&~(Flag.LOCAL.asInstanceOf[Long])
                  .&~(Flag.CASEACCESSOR.asInstanceOf[Long])
                  .asInstanceOf[Modifiers]
                else mods
              ValDef(newMods, name, tpt, rhs)

            case e => e
          }

        val fields =
          annottees match {
            case (q"$_ class $_[..$_] $_(..$fields)(implicit ..$_) extends { ..$_ } with ..$_ { $_ => ..$_ }") ::
                 Nil => fields
            case e => e
          }

        val newTree = ClassDef(mods, className, tparams, Template(parents, self, newBody))

        val untypedClassName = "Untyped" + className.toString
        q"""
           $newTree

           case class ${TypeName(
            untypedClassName
          )}(..$fields) extends ai.nikin.pipeline.model.dsl.UntypedVertex


         """
    }
  }
}
