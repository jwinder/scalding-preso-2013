package com.banno.drawing
import spray.routing._
import spray.json.DefaultJsonProtocol
import spray.httpx.marshalling._
import spray.httpx.SprayJsonSupport._
import spray.http._
import akka.actor.ActorSystem
import org.joda.time.DateTime

case class DrawingPart(ms: Long, x: Int, y: Int) {
  lazy val time = new DateTime(ms)
}

case class Drawing(parts: Seq[DrawingPart]) {
  val drawingId = NewUuid()
  val time = if (parts.nonEmpty) parts.minBy(_.ms).time else DateTime.now
}

object DrawingJsonFormats extends DefaultJsonProtocol {
  implicit val DrawingPartFormat = jsonFormat(DrawingPart, "ms", "x", "y")
  implicit val DrawingFormat = jsonFormat(Drawing, "parts")
}

object HttpServer extends SimpleRoutingApp with HdfsDrawingsOutput {
  import DrawingJsonFormats._

  implicit val DrawingActorSystem = ActorSystem("drawing")

  def main(ss: Array[String]) {
    ensureHdfsDrawingDirectory()

    startServer("0.0.0.0", 8080) {
      corsPreflightCheck(path("drawings")) ~
      (post & path("drawings") & entity(as[Drawing])) { drawing =>
        writeDrawingToHdfs(drawing)
        complete(StatusCodes.OK, Array.empty[Byte])
      }
    }
  }

  // meh
  def CORS = optionalHeaderValueByName("origin") flatMap { maybeOrigin =>
    val origin: String = maybeOrigin.getOrElse("origin")
    respondWithHeaders(HttpHeaders.`Access-Control-Allow-Origin`(origin),
                       HttpHeaders.`Access-Control-Allow-Headers`("origin", "x-requested-with", "content-type"),
                       HttpHeaders.`Access-Control-Allow-Credentials`(true))
  }
  def corsPreflightCheck(path: Directive0) = (options & path) {
    CORS & complete(StatusCodes.OK, Array.empty[Byte])
  }
}

object NewUuid {
  def apply() = java.util.UUID.randomUUID().toString
}
