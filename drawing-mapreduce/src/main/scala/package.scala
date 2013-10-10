package com.banno
import org.joda.time.DateTime

package object drawing {

  final val NameNode = "hdfs://33.33.33.11/"

  def toDateHour(time: DateTime) = time.toMutableDateTime.hourOfDay.roundFloor.toDateTime
}
