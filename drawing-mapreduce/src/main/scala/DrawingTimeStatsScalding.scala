package com.banno.drawing
import com.twitter.scalding.{Job, Args, TextLine}
import org.joda.time.{DateTime, Duration}

class DrawingTimeStatsScalding(args: Args) extends Job(args) {
  TextLine(NameNode + "drawings")
    .read
    .mapTo('line -> ('time, 'drawingId)) { (line: String) =>
      val point = Point.parseFromHdfsLine(line)
      (point.time, point.drawingId)
    }
    .groupBy('drawingId) { group =>
      group.mapReduceMap(('time, 'drawingId) -> 'drawingDurationMs) { (data: (DateTime, String)) =>
        val (time, drawingId) = data
        DrawingTime(drawingId, time, time)
      }(_ ++ _)(_.duration.getMillis)
    }
    .groupAll { group =>
      group.average('drawingDurationMs -> 'averageDrawingTime)
           .min('drawingDurationMs -> 'minDrawingTime)
           .max('drawingDurationMs -> 'maxDrawingTime)
    }
    .write(TextLine(NameNode + "scalding/drawing-time-stats"))
}

case class DrawingTime(drawingId: String, start: DateTime, end: DateTime) {
  def ++ (other: DrawingTime) =
    copy(start = if (start isBefore other.start) start else other.start,
         end = if (end isAfter other.end) end else other.end)

  lazy val duration = new Duration(start, end)
}
