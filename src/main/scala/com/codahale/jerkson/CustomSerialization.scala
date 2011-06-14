package com.codahale.jerkson

import util.DynamicVariable
import org.codehaus.jackson.map.{Serializers, Deserializers}

object CustomSerializers extends DynamicVariable[Option[Serializers]](None)

object CustomDeserializers extends DynamicVariable[Option[Deserializers]](None)
