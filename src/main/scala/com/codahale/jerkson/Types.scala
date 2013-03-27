package com.codahale.jerkson

import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConversions.mapAsScalaConcurrentMap

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.`type`.{TypeFactory, ArrayType}

private[jerkson] object Types {
  private val cachedTypes = mapAsScalaConcurrentMap(new ConcurrentHashMap[Manifest[_], JavaType]())

  def build(factory: TypeFactory, manifest: Manifest[_]): JavaType =
    cachedTypes.getOrElseUpdate(manifest, constructType(factory, manifest))

  private def constructType(factory: TypeFactory, manifest: Manifest[_]): JavaType = {
    if (manifest.runtimeClass.isArray) {
      ArrayType.construct(factory.constructType(manifest.runtimeClass.getComponentType), null, null)
    } else {
      factory.constructParametricType(
        manifest.runtimeClass,
        manifest.typeArguments.map {m => build(factory, m)}.toArray: _*)
    }
  }
}
