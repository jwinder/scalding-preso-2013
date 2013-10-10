package com.banno.drawing
import org.joda.time.DateTime

case class Point2D(x: Int, y: Int) {
  def translate(dx: Int, dy: Int) = copy(x = x + dx, y = y + dy)
}

object Point2D {
  def parseFromHdfsLine(line: String): Point2D = {
    val Array(drawingId, time, x, y) = line.split(',')
    new Point2D(x.toInt, y.toInt)
  }
}

case class Point3D(x: Int, y: Int, time: DateTime) {
  lazy val hour = toDateHour(time)
  def translate(dx: Int, dy: Int) = copy(x = x + dx, y = y + dy)
  def withTime(time: DateTime) = copy(time = time)
}

object Point3D {
  def parseFromHdfsLine(line: String): Point3D = {
    val Array(drawingId, time, x, y) = line.split(',')
    new Point3D(x.toInt, y.toInt, new DateTime(time.toLong))
  }
}

case class Point(drawingId: String, x: Int, y: Int, time: DateTime)

object Point {
  def parseFromHdfsLine(line: String): Point = {
    val Array(drawingId, time, x, y) = line.split(',')
    new Point(drawingId, x.toInt, y.toInt, new DateTime(time.toLong))
  }
}
