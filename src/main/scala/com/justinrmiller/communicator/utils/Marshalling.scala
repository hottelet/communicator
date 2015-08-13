package com.justinrmiller.communicator.utils

import java.util.UUID

import org.joda.time.DateTime
import org.joda.time.format.{ISODateTimeFormat, DateTimeFormatter}
import spray.json._

trait Marshalling {
  implicit object UuidJsonFormat extends JsonFormat[UUID] {
    def write(x: UUID) = JsString(x toString ())
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit object DateJsonFormat extends RootJsonFormat[DateTime] {
    private val parserISO : DateTimeFormatter = ISODateTimeFormat.dateTime();

    override def write(obj: DateTime) = JsString(parserISO.print(obj))
    override def read(json: JsValue) : DateTime = json match {
      case JsString(s) => parserISO.parseDateTime(s)
      case x => deserializationError("Expected DateTime as JsString, but got " + x)
    }
  }
}