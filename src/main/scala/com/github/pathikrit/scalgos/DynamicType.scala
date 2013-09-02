package com.github.pathikrit.scalgos

import collection.mutable
import DynamicType._

/**
 * An useful dynamic type that let's you add/delete fields and methods during runtime to a structure
 */
class DynamicType extends Dynamic {

  private val fields = mutable.Map.empty[String, Any] withDefault { key => throw new NoSuchFieldError(key) }
  private val methods = mutable.Map.empty[String, GenFn] withDefault { key => throw new NoSuchMethodError(key) }

  def selectDynamic(key: String) = fields(key)

  def updateDynamic(key: String)(value: Any) = value match {
    case fn0: Function0[Any] => methods(key) = {case Seq() => fn0()}
    case fn1: Function1[Any, Any] => methods(key) = fn1
    case fn2: Function2[Any, Any, Any] => methods(key) = fn2
    case _ => fields(key) = value
  }

  def applyDynamic(key: String)(args: Any*) = methods(key)(args)

  /**
   * Deletes a field (methods are fields too)
   * @return the old field value
   */
  def delete(key: String) = fields.remove(key)

  //todo: export/print to json
}

object DynamicType {
  import reflect.ClassTag

  type GenFn = PartialFunction[Seq[Any],Any]
  implicit def toGenFn1[A: ClassTag](f: (A) => Any): GenFn = { case Seq(a: A) => f(a) }
  implicit def toGenFn2[A: ClassTag, B: ClassTag](f: (A, B) => Any): GenFn = { case Seq(a: A, b: B) => f(a, b) }
  // todo: generalize to 22-args
}
