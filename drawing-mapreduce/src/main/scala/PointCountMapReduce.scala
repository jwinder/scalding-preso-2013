package com.banno.drawing
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf._
import org.apache.hadoop.io._
import org.apache.hadoop.mapred._
import org.apache.hadoop.util._
import org.joda.time.DateTime

class PointCountMapReduce

object PointCountMapReduce {
  //def main(ss: Array[String]) {
    val conf = new JobConf(classOf[PointCountMapReduce])
    conf.setJobName("point-count-map-reduce-%s".format(DateTime.now.toString()))

    // outputs of mapper
    conf.setMapOutputKeyClass(classOf[Text])
    conf.setMapOutputValueClass(classOf[IntWritable])

    // outputs of reducer
    conf.setOutputKeyClass(classOf[Text])
    conf.setOutputValueClass(classOf[LongWritable])

    // set classes
    conf.setMapperClass(classOf[PointCountMapper])
    conf.setReducerClass(classOf[PointCountReducer])

    FileInputFormat.setInputPaths(conf, new Path(NameNode + "drawings"))
    FileOutputFormat.setOutputPath(conf, new Path(NameNode + "mapreduce/point-count"))

    JobClient.runJob(conf)
  //}
}

class PointCountMapper extends MapReduceBase with Mapper[LongWritable, Text, Text, IntWritable] {
  def map(key: LongWritable, value: Text, collector: OutputCollector[Text, IntWritable], reporter: Reporter) = {
    val point = Point2D.parseFromHdfsLine(value.toString())
    collector.collect(new Text(point.x + ":" + point.y), new IntWritable(1))
  }
}

class PointCountReducer extends MapReduceBase with Reducer[Text, IntWritable, Text, LongWritable] {
  import scala.collection.JavaConversions._
  def reduce(key: Text, values: java.util.Iterator[IntWritable], collector: OutputCollector[Text, LongWritable], reporter: Reporter) = {
    collector.collect(key, new LongWritable(values.size))
  }
}
