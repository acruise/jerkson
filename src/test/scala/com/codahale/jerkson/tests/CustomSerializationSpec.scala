package com.codahale.jerkson.tests

import com.codahale.simplespec
import simplespec._
import org.codehaus.jackson.`type`.JavaType
import org.codehaus.jackson.map._
import `type`.{MapType, SimpleType}
import org.codehaus.jackson.{JsonGenerator, JsonParser}
import com.codahale.jerkson.{CustomDeserializers, Json, CustomSerializers}

object CustomSerializationSpec extends Spec {
  val serializ0rz = new Serializers {
    def findSerializer(config: SerializationConfig, jtype: JavaType, bdesc: BeanDescription, bprop: BeanProperty) = {
      jtype match {
        case st: SimpleType if st.getRawClass == classOf[ONoes] =>
          new JsonSerializer[ONoes] {
            def serialize(value: ONoes, jgen: JsonGenerator, provider: SerializerProvider) {
              jgen.writeStartObject()
              jgen.writeNumberField("theInt", value.i)
              jgen.writeStringField("theString", value.s)
              jgen.writeEndObject()
            }
          }
        case other => null
      }
    }
  }

  val deserializ0rz = new Deserializers.None {
    override def findMapDeserializer(mtype: MapType, config: DeserializationConfig, provider: DeserializerProvider, beanDesc: BeanDescription, property: BeanProperty, keyDeserializer: KeyDeserializer, elementTypeDeserializer: TypeDeserializer, elementDeserializer: JsonDeserializer[_]): JsonDeserializer[_] = {
      return null
    }

    override def findBeanDeserializer(jtype: JavaType, config: DeserializationConfig, provider: DeserializerProvider, beanDesc: BeanDescription, property: BeanProperty): JsonDeserializer[_] = {
      jtype match {
        case st: SimpleType if st.getRawClass == classOf[ONoes] =>
          new JsonDeserializer[ONoes] {
            def deserialize(jp: JsonParser, ctxt: DeserializationContext) = {
              val node = jp.readValueAsTree()
              val theInt = node.get("theInt").getIntValue
              val theString = node.get("theString").getTextValue
              ONoes(theInt, theString)
            }

          }
        case other => null
      }
    }
  }

  case class ONoes(i: Int, s: String)

  class `Custom Deserialization` {
    def `should work OK this way too` {
      CustomDeserializers.withValue(Some(deserializ0rz)) {
        val s = """{"theInt": 2, "theString": "two"}"""
        val got = Json.parse[ONoes](s)
        println(got)
        got must beEqual(ONoes(2,"two"))
      }
    }
  }

  class `Custom Serialization` {
    def `should work OK` {
      CustomSerializers.withValue(Some(serializ0rz)) {
        val o = ONoes(1,"one")
        val got = Json.generate(o)
        println(got)
        got must beEqual("""{"theInt":1,"theString":"one"}""")
      }
    }
  }


}