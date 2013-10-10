package com.banno.drawing
import com.twitter.scalding.{Job, Args, TextLine, CascadeJob}
import com.twitter.algebird.Monoid
import org.joda.time.DateTime

class ScaldingExampleCascade(args: Args) extends CascadeJob(args) {
  def jobs = List(
    new MapScaldingExample(args),
    new MapToScaldingExample(args),
    new FlatMapScaldingExample(args),
    new FlatMapToScaldingExample(args),
    new ProjectScaldingExample(args),
    new DiscardScaldingExample(args),
    new FilterScaldingExample(args),
    new GroupByFoldLeftScaldingExample(args),
    new GroupByMapReduceMapScaldingExample(args),
    new GroupByMapPlusMapScaldingExample(args),
    new GroupByScanLeftScaldingExample(args)
  )
}

class MapScaldingExample(args: Args) extends Job(args) {
  TextLine(NameNode + "drawings")
    .read
    .map('line -> 'point) { (line: String) => Point2D.parseFromHdfsLine(line) }
    .write(TextLine(NameNode + "scalding/map-scalding-example"))
}

class MapToScaldingExample(args: Args) extends Job(args) {
  TextLine(NameNode + "drawings")
    .read
    .mapTo('line -> 'point) { (line: String) => Point2D.parseFromHdfsLine(line) }
    .write(TextLine(NameNode + "scalding/mapTo-scalding-example"))
}

class FlatMapScaldingExample(args: Args) extends Job(args) {
  TextLine(NameNode + "drawings")
    .read
    .flatMap('line -> 'point) { (line: String) =>
      val point = Point2D.parseFromHdfsLine(line)
      for {
        dx <- -5 to 5
        dy <- -5 to 5
      } yield point.translate(dx, dy)
    }
    .write(TextLine(NameNode + "scalding/flatMap-scalding-example"))
}

class FlatMapToScaldingExample(args: Args) extends Job(args) {
  TextLine(NameNode + "drawings")
    .read
    .flatMapTo('line -> 'point) { (line: String) =>
      val point = Point2D.parseFromHdfsLine(line)
      for {
        deltaX <- -5 to 5
        deltaY <- -5 to 5
      } yield point.translate(deltaX, deltaY)
    }
    .write(TextLine(NameNode + "scalding/flatMapTo-scalding-example"))
}

class ProjectScaldingExample(args: Args) extends Job(args) {
  TextLine(NameNode + "drawings")
    .read
    .map('line -> 'point) { (line: String) => Point2D.parseFromHdfsLine(line) }
    .project('point)
    .write(TextLine(NameNode + "scalding/project-scalding-example"))
}

class DiscardScaldingExample(args: Args) extends Job(args) {
  TextLine(NameNode + "drawings")
    .read
    .map('line -> 'point) { (line: String) => Point2D.parseFromHdfsLine(line) }
    .discard('line)
    .write(TextLine(NameNode + "scalding/discard-scalding-example"))
}

class FilterScaldingExample(args: Args) extends Job(args) {
  TextLine(NameNode + "drawings")
    .read
    .mapTo('line -> 'point) { (line: String) => Point2D.parseFromHdfsLine(line) }
    .filter('point) { (point: Point2D) => point.x % 2 == 0 && point.y % 2 == 0 }
    .write(TextLine(NameNode + "scalding/filter-scalding-example"))
}

class GroupByFoldLeftScaldingExample(args: Args) extends Job(args) {
  TextLine(NameNode + "drawings")
    .read
    .mapTo('line -> ('x, 'y)) { (line: String) =>
      val point = Point2D.parseFromHdfsLine(line)
      (point.x, point.y)
    }
    .groupBy('x) { group =>
      group.foldLeft(('x, 'y) -> 'ySum)(0) { (acc, next: (Int, Int)) =>
        acc + next._2
      }
    }
    .write(TextLine(NameNode + "scalding/groupBy-foldLeft-scalding-example"))
}

class GroupByMapReduceMapScaldingExample(args: Args) extends Job(args) {
  TextLine(NameNode + "drawings")
    .read
    .mapTo('line -> ('hour, 'point)) { (line: String) =>
      val point = Point3D.parseFromHdfsLine(line)
      (point.hour, point)
    }
    .groupBy('hour) { group =>
      group.mapReduceMap(('hour, 'point) -> ('xAvg, 'yAvg)) { (data: (DateTime, Point3D)) =>
        val (hour, point) = data
        (point.x, point.y, 1, hour)
      } { (left, right) =>
        (left._1 + right._1, left._2 + right._2, left._3 + right._3, left._4)
      } { reduced =>
        val (xSum, ySum, totalPairs, hour) = reduced
        (xSum.toDouble / totalPairs, ySum.toDouble / totalPairs)
      }
    }
    .write(TextLine(NameNode + "scalding/groupBy-mapReduceMap-scalding-example"))
}

class GroupByMapPlusMapScaldingExample(args: Args) extends Job(args) {

  implicit lazy val tuple4Monoid = new Monoid[(Int, Int, Int, DateTime)] {
    def plus(left: (Int, Int, Int, DateTime), right: (Int, Int, Int, DateTime)) =
      (left._1 + right._1, left._2 + right._2, left._3 + right._3, left._4)

    def zero = ???
  }

  TextLine(NameNode + "drawings")
    .read
    .mapTo('line -> ('hour, 'point)) { (line: String) =>
      val point = Point3D.parseFromHdfsLine(line)
      (point.hour, point)
    }
    .groupBy('hour) { group =>
      group.mapPlusMap(('hour, 'point) -> ('xAvg, 'yAvg)) { (data: (DateTime, Point3D)) =>
        val (hour, point) = data
        (point.x, point.y, 1, hour)
      } { reduced =>
        val (xSum, ySum, totalPairs, hour) = reduced
        (xSum.toDouble / totalPairs, ySum.toDouble / totalPairs)
      }
    }
    .write(TextLine(NameNode + "scalding/groupBy-mapPlusMap-scalding-example"))
}

class GroupByScanLeftScaldingExample(args: Args) extends Job(args) {
  TextLine(NameNode + "drawings")
    .read
    .map('line -> ('hour, 'point)) { (line: String) =>
      val point = Point3D.parseFromHdfsLine(line)
      (point.hour, point)
    }
    .groupBy('hour) { group =>
      val initial = (Point3D(0, 0, new DateTime(0)), 0)
      group.scanLeft(('hour, 'point) -> ('sumPoint, 'total))(initial) { (acc, (next: (DateTime, Point3D))) =>
        val (hour, point) = next
        val (sumPoint, total) = acc
        (sumPoint.translate(point.x, point.y).withTime(hour), total + 1)
      }
    }
    .mapTo(('sumPoint, 'total) -> 'avgPoint) { (data: (Point3D, Int)) =>
      val (sumPoint, total) = data
      val x = sumPoint.x.toDouble
      val y = sumPoint.y.toDouble
      sumPoint.copy(x = x / total toInt, y = y / total toInt)
    }
    .write(TextLine(NameNode + "scalding/groupBy-scanLeft-scalding-example"))
}
