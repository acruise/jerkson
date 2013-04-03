package com.codahale.jerkson.ser

import java.lang.reflect.{Field, Modifier}

import language.existentials

import com.fasterxml.jackson.databind.{SerializerProvider, JsonSerializer}
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties}
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.jsontype.TypeSerializer

import com.codahale.jerkson.JsonSnakeCase
import com.codahale.jerkson.Util._

class CaseClassSerializer[A <: Product](klass: Class[_]) extends JsonSerializer[A] {
  private val isSnakeCase = klass.isAnnotationPresent(classOf[JsonSnakeCase])
  private val ignoredFields = if (klass.isAnnotationPresent(classOf[JsonIgnoreProperties])) {
    klass.getAnnotation(classOf[JsonIgnoreProperties]).value().toSet
  } else Set.empty[String]
  
  private def ignoreField(f: Field): Boolean = {
    f.isAnnotationPresent(classOf[JsonIgnore]) ||
      ignoredFields(f.getName) ||
      (f.getModifiers & Modifier.TRANSIENT) != 0 ||
      f.getName.contains("$")
  }

  private val nonIgnoredFields = {
    // TODO this should be an unfold but I R not SMRT
    var soup = klass.getSuperclass
    var fields = List(klass.getDeclaredFields.filterNot(ignoreField _)) // No _* on purpose

    while (soup != null) {
      fields ::= soup.getDeclaredFields.filterNot(ignoreField _).reverse
      soup = soup.getSuperclass
    }

    fields.flatten
  }

  private val methods = klass.getDeclaredMethods
                                .filter { _.getParameterTypes.isEmpty }
                                .map { m => m.getName -> m }.toMap

  override def serializeWithType(value: A, json: JsonGenerator, provider: SerializerProvider, typeSerializer: TypeSerializer) {
    typeSerializer.writeTypePrefixForObject(value, json)
    doSerialize(value, json, provider)
    typeSerializer.writeTypeSuffixForObject(value, json)
  }

  def serialize(value: A, json: JsonGenerator, provider: SerializerProvider) {
    json.writeStartObject()
    doSerialize(value, json, provider)
    json.writeEndObject()
  }

  def doSerialize(value: A, json: JsonGenerator, provider: SerializerProvider) {
    for (field <- nonIgnoredFields) {
      field.setAccessible(true) // DIRTY BIRD
      val methodOpt = methods.get(field.getName)
      val fieldValue: Object = methodOpt.map { _.invoke(value) }.getOrElse(field.get(value))
      if (fieldValue != None) {
        val fieldName = methodOpt.map { _.getName }.getOrElse(field.getName)
        provider.defaultSerializeField(if (isSnakeCase) snakeCase(fieldName) else fieldName, fieldValue, json)
      }
    }
  }

}
