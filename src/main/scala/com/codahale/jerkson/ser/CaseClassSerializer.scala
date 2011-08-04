package com.codahale.jerkson.ser

import org.codehaus.jackson.JsonGenerator
import org.codehaus.jackson.map.{SerializerProvider, JsonSerializer}
import org.codehaus.jackson.annotate.JsonIgnore
import org.codehaus.jackson.map.annotate.JsonCachable
import java.lang.reflect.{Field, Modifier}

@JsonCachable
class CaseClassSerializer[A <: Product](klass: Class[_]) extends JsonSerializer[A] {
  private def ignoreField(f: Field): Boolean = {
      f.isAnnotationPresent(classOf[JsonIgnore]) || (f.getModifiers & Modifier.TRANSIENT) != 0 || f.getName.contains("$")
  }

  private val nonIgnoredFields = Option(klass.getSuperclass).map(_.getDeclaredFields.filterNot(ignoreField _)).flatten.toList ++
                                 klass.getDeclaredFields.filterNot(ignoreField _)

  private val methods = klass.getDeclaredMethods
                                .filter { m => m.getParameterTypes.isEmpty && !m.getName.contains("$") }
                                .map { m => m.getName -> m }.toMap

  def serialize(value: A, json: JsonGenerator, provider: SerializerProvider) {
    json.writeStartObject()
    for (field <- nonIgnoredFields) {
      field.setAccessible(true) // DIRTY BIRD
      val methodOpt = methods.get(field.getName)
      val fieldValue: Object = methodOpt.map { _.invoke(value) }.getOrElse(field.get(value))
      if (fieldValue != None) {
        val fieldName = methodOpt.map { _.getName }.getOrElse(field.getName)
        provider.defaultSerializeField(fieldName, fieldValue, json)
      }
    }
    json.writeEndObject()
  }
}
