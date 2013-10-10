name := "drawing-server"

version := "1.0"

scalaVersion := "2.10.1"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "spray" at "http://repo.spray.io/"

libraryDependencies ++= Seq(
  "org.joda" % "joda-convert" % "1.1",
  "joda-time" % "joda-time" % "2.0",
  "io.spray" % "spray-can" % "1.1-20130723",
  "io.spray" % "spray-routing" % "1.1-20130723",
  "io.spray" % "spray-httpx" % "1.1-20130723",
  "io.spray" %% "spray-json" % "1.2.5",
  "com.typesafe.akka" % "akka-actor_2.10" % "2.1.4",
  "org.apache.hadoop" % "hadoop-common" % "2.0.0-cdh4.3.0",
  "org.apache.hadoop" % "hadoop-client" % "2.0.0-cdh4.3.0",
  "org.apache.hbase" % "hbase" % "0.94.6-cdh4.3.0",
  "org.slf4j" % "slf4j-log4j12" % "1.6.4",
  "org.slf4j" % "jcl-over-slf4j" % "1.6.4" // for commons-logging dep of hbase
)
