package com.banno.drawing
import com.twitter.scalding.{Job, Args, TextLine}

class PointCountScalding(args: Args) extends Job(args) {
  TextLine(NameNode + "drawings")
    .mapTo('line -> 'point) { (line: String) => Point2D.parseFromHdfsLine(line) }
    .groupBy('point)(_.size('total))
    .write(TextLine(NameNode + "scalding/point-count"))
}
