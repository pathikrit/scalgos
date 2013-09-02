package com.github.pathikrit.scalgos

import org.specs2.mutable.Specification

class DynamicTypeSpec extends Specification {

  "DynamicType" should {

    "add dynamic fields" in {
      val x = new DynamicType
      x.someField = 39
      x.someField must be equalTo 39
      x.someField = "is a string now!"
      x.someOtherField = "hello world!"
      x.someField must be equalTo "is a string now!"
      x.someOtherField must be equalTo "hello world!"
      x.aNonExistendField must throwA[NoSuchFieldError]
    }

    "add dynamic methods" in {
      val x = new DynamicType
      x.aMethodWithNoArg = () => "A method with no arg called"
      x.aMethodWithOneArg = (x: String) => s"A method called with arg1=$x"
      x.aMethodWithTwoArgs = (x: String, y: Int) => s"A method called with arg1(string)=$x and arg2(int)=$y"

      x.aMethodWithNoArg must throwA[NoSuchFieldError]
      x.aMethodWithNoArg() must be equalTo "A method with no arg called"
      x.aMethodWithOneArg("hi") must be equalTo s"A method called with arg1=hi"
      x.aMethodWithTwoArgs("hello", 42) must be equalTo s"A method called with arg1(string)=hello and arg2(int)=42"
      x.missingMethod() must throwA[NoSuchMethodError]
    }

    "support method overloading" in {
//      val x = new DynamicType
//      x.func = (s: String) => s"I am a string = $s"
//      x.func = (i: Int) => s"I am an int = $i"
//
//      x.func("hello") must be equalTo "I am a string = hello"
//      x.func(42) must be equalTo "I am an int = 42"
//
//      x.func() aka "too few args" must throwA[NoSuchMethodError]
//      x.func(42, "hello") aka "too many args" must throwA[NoSuchMethodError]
//      x.func(false) aka "wrong type" must throwA[NoSuchMethodError]
      todo
    }

    "allow dynamic methods to access dynamic fields" in {
      val x = new DynamicType
      x.name = "Rihanna"
      x.greet = (who: String) => s"My name is ${x.name}! Nice to meet you, $who"

      x.greet("Rick") must be equalTo "My name is Rihanna! Nice to meet you, Rick"
      x.name = "Beyonce"
      x.greet("Rick") must be equalTo "My name is Beyonce! Nice to meet you, Rick"
    }

    "delete fields" in {
      val x = new DynamicType
      x.name = "Rick"
      x.name must be equalTo "Rick"
      x.delete("name") must beSome("Rick")
      x.name must throwA[NoSuchFieldError]
    }

    "delete methods" in {
      val x = new DynamicType
      x.greet = () => "Hi!"
      x.greet() must be equalTo "Hi!"
      //x.delete("greet") must beSome.which(f => f.isInstanceOf[Function0[Any]])
      //x.greet() must throwA[NoSuchMethodError]
      todo
    }

    "handle deletes when fields and methods have same names" in {
      val x =  new DynamicType
      x.foo = "a field"
      x.foo = () => "a method"

      x.foo must be equalTo "a field"
      x.foo() must be equalTo "a method"
      todo
    }

    "cannot change special methods" in {
      val x = new DynamicType
      x.delete("delete") must beNone
      x.name = "hi"
      x.name must be equalTo "hi"
      x.delete("name") aka "delete still works" must beSome("hi")
    }
  }
}
