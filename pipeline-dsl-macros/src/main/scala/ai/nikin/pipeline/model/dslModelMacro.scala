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
        //          case (cls@q"$_ class $className[..$tparams] $_(..$fields)(implicit ..$ifields) extends { ..$_ } with ..$_ { $_ => ..$_ }") ::
        //            Nil =>
        val newBody =
          body.map {
            case ValDef(mods, name, tpt, rhs) =>
              // look here
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

        val filteredFields =
          annottees match {
            case (q"$_ class $className[..$tparams] $_(..$fields)(implicit ..$ifields) extends { ..$_ } with ..$_ { $_ => ..$_ }") ::
                 Nil => fields
            case e => e
          }

//        val filteredFields = body.filter {
//          case ValDef(mods, _, _, _) =>
//            !(mods.hasFlag(Flag.IMPLICIT))
//          case _ =>
//            false
//        }

        //            val filteredFields = body.filter {
        //              case ValDef(mods, name, tpt, rhs) =>
        //                tpt match {
        //                  case tq"$fieldType" =>
        //                    val tpe = c.typecheck(tq"$fieldType", mode = c.TYPEmode, silent = true) match {
        //                      case EmptyTree => println(s"can't typecheck $fieldType while expanding @DslModel for $className"); NoType
        //                      case t => t.tpe
        //                    }
        //                    !(tpe <:< typeOf[Boolean])
        //                  case _ =>
        //                    true
        //                }
        ////              case q"$mods val $name: $tpt = $rhs" =>
        ////                println("xxxxx")
        ////                name.toString() != "boolean"
        ////
        ////              case q"$name: $tpt" =>
        ////                println("yyyyyy")
        ////                name.toString() != "boolean"
        //              case _ =>
        //                false
        //            }

        //            val filteredImplicitFields = ifields.map { case ValDef(mods, name, tpt, rhs) =>
        //              val newMods = mods.asInstanceOf[scala.reflect.internal.Trees#Modifiers].&~(Flag.LOCAL.asInstanceOf[Long]).&~(Flag.CASEACCESSOR.asInstanceOf[Long]).asInstanceOf[Modifiers]
        //              ValDef(newMods, name, tpt, rhs)
        //            }.filter {
        //              case field@q"$mods val $name: $tpt = $rhs" =>
        //
        //                tpt match {
        //                  case tq"$fieldType" =>
        //                    val tpe = c.typecheck(tq"$fieldType", mode = c.TYPEmode, silent = true) match {
        //                      case EmptyTree => println(s"can't typecheck $fieldType while expanding @DslModel for $className"); NoType
        //                      case t => t.tpe
        //                    }
        //                    !(tpe <:< typeOf[Boolean])
        //                  case _ =>
        //                    true
        //                }
        //
        //              //              case q"$mods val $name: $tpt = $rhs" =>
        //              //                println("xxxxx")
        //              //                name.toString() != "boolean"
        //              //
        //              //              case q"$name: $tpt" =>
        //              //                println("yyyyyy")
        //              //                name.toString() != "boolean"
        //              case _ =>
        //                true
        //            }

        val newTree = ClassDef(mods, className, tparams, Template(parents, self, newBody))

        println(filteredFields)

        q"""
           $newTree

           case class ${TypeName(
            "Untyped" + className.toString
          )}(..$filteredFields) extends ai.nikin.pipeline.model.DSL.UntypedVertex
         """
    }
  }
}

//                  case tq"$first[..${List(second)}]" =>
//                    val firstType = c.typecheck(tq"$first", mode = c.TYPEmode, silent = true) match {
//                      case EmptyTree => println(s"can't typecheck $first while expanding @RemoveOptionFromFields for $className"); NoType
//                      case t => t.tpe
//                    }
//                    if (firstType <:< typeOf[Option[_]].typeConstructor) {
//                      val secondSymbol = c.typecheck(tq"$second", mode = c.TYPEmode, silent = true) match {
//                        case EmptyTree => println(s"can't typecheck $second while expanding @RemoveOptionFromFields for $className"); NoSymbol
//                        case t => t.symbol
//                      }
//                      if (secondSymbol.isClass && secondSymbol.asClass.isCaseClass) {
//                        val secondClassFields = secondSymbol.typeSignature.decls.toList.filter(s => s.isMethod && s.asMethod.isCaseAccessor)
//                        secondClassFields.foreach(s =>
//                          c.typecheck(q"$s", silent = true) match {
//                            case EmptyTree => println(s"can't typecheck $s while expanding @RemoveOptionFromFields for $className")
//                            case t => println(s"field ${t.symbol} of type ${t.tpe}, subtype of Option: ${t.tpe <:< typeOf[Option[_]]}")
//                          }
//                        )
//                      }
//                      q"$mods val $name: $second = $rhs"
//                    } else field
