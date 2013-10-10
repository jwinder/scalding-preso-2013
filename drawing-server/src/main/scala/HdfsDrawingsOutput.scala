package com.banno.drawing
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.commons.io.IOUtils

// todo; sneakily chmod the hdfs root directory to make this work
// usually, the user running the application would be set up with write permissions to the hdfs directory

trait HdfsDrawingsOutput {

  private[this] val baseUrl = "hdfs://33.33.33.11/"
  private[this] val dir = "drawings"
  private[this] val url = baseUrl + dir
  private[this] val config = new Configuration {
    set(FileSystem.FS_DEFAULT_NAME_KEY, baseUrl)
  }
  private[this] val hdfs = FileSystem.get(config)

  def ensureHdfsDrawingDirectory() = {
    val path = new Path(url)
    if (!hdfs.exists(path)) {
      hdfs.mkdirs(path)
    }
  }

  def writeDrawingToHdfs(drawing: Drawing) = {
    val path = new Path(url + "/" + drawing.drawingId)
    val contents = drawing.parts.sortBy(_.ms).map(part => drawing.drawingId + "," + part.ms + "," + part.x + "," + part.y).mkString("\n")
    val stream = hdfs.create(path)
    try {
      stream.write(contents.getBytes("utf-8"))
    } finally stream.close()
  }
}
