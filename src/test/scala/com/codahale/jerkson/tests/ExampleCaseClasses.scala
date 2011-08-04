package com.codahale.jerkson.tests

import org.codehaus.jackson.annotate.JsonIgnore
import org.codehaus.jackson.JsonNode

case class CaseClass(id: Long, name: String)

case class CaseClassWithLazyVal(id: Long) {
  lazy val woo = "yeah"
}

case class CaseClassWithIgnoredField(id: Long) {
  @JsonIgnore
  val uncomfortable = "Bad Touch"
}
case class CaseClassWithTransientField(id: Long) {
  @transient
  val lol = "I'm sure it's just a phase."
}

case class CaseClassWithOverloadedField(id: Long) {
  def id(prefix: String): String = prefix + id
}

case class CaseClassWithOption(value: Option[String])

case class CaseClassWithJsonNode(value: JsonNode)

case class CaseClassWithAllTypes(map: Map[String, String],
                                 set: Set[Int],
                                 string: String,
                                 list: List[Int],
                                 seq: Seq[Int],
                                 indexedSeq: IndexedSeq[Int],
                                 vector: Vector[Int],
                                 bigDecimal: BigDecimal,
                                 bigInt: BigInt,
                                 int: Int,
                                 long: Long,
                                 char: Char,
                                 bool: Boolean,
                                 short: Short,
                                 byte: Byte,
                                 float: Float,
                                 double: Double,
                                 any: Any,
                                 anyRef: AnyRef,
                                 intMap: Map[Int, Int],
                                 longMap: Map[Long, Long])

object OuterObject {
  case class NestedCaseClass(id: Long)

  object InnerObject {
    case class SuperNestedCaseClass(id: Long)
  }
}

case class Unfortunate(i: Int) {
  @JsonIgnore
  def this() = this(-1)
}

case class DownrightSad(j: Int) {
  def this(k: Long) = this(k.toInt)
}

abstract class InheritedMutable {
  var pokeMe: Int = 0
}

case class CaseClassWithInheritedMutable(s: String) extends InheritedMutable

abstract class InheritedVal(val gene: String)
case class CaseClassWithOverrideVal(override val gene: String, bob: Int) extends InheritedVal(gene)