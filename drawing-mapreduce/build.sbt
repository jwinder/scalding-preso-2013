import AssemblyKeys._

name := "drawing-mapreduce"

version := "1.0"

scalaVersion := "2.10.1"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "spray" at "http://repo.spray.io/"

libraryDependencies ++= Seq(
  "org.joda" % "joda-convert" % "1.4",
  "joda-time" % "joda-time" % "2.3",
  "io.spray" % "spray-can" % "1.1-20130723",
  "io.spray" % "spray-routing" % "1.1-20130723",
  "io.spray" % "spray-httpx" % "1.1-20130723",
  "io.spray" %% "spray-json" % "1.2.5",
  "com.typesafe.akka" % "akka-actor_2.10" % "2.1.4",
  "org.apache.hadoop" % "hadoop-common" % "2.0.0-cdh4.3.0",
  "org.apache.hadoop" % "hadoop-client" % "2.0.0-cdh4.3.0",
  "org.apache.hbase" % "hbase" % "0.94.6-cdh4.3.0",
  "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "2.0.0-cdh4.3.0",
  "cascading" % "cascading-core" % "2.1.6" % "provided",
  "cascading" % "cascading-local" % "2.1.6" % "provided",
  "cascading" % "cascading-hadoop" % "2.1.6" % "provided",
  "com.twitter" %% "scalding-core" % "0.8.5",
  "com.twitter" % "maple" % "0.2.7",
  "org.slf4j" % "slf4j-log4j12" % "1.6.4",
  "org.slf4j" % "jcl-over-slf4j" % "1.6.4" // for commons-logging dep of hbase
)

assemblySettings

mergeStrategy in assembly ~= { (old) =>
  {
    case "project.clj" => MergeStrategy.concat
    case "about.html" => MergeStrategy.first
    case "overview.html" => MergeStrategy.first
    case x => old(x)
  }
}

ivyXML :=
  <dependencies>
    <exclude org="org.slf4j" module="slf4j-simple"/>
    <exclude org="org.slf4j"  module="log4j-over-slf4j"/>
    <exclude org="commons-logging" module="commons-logging"/>
    <exclude org="ch.qos.logback" module="logback-classic"/>
    <override org="commons-daemon" module="commons-daemon" rev="1.0.15"/>
    <override org="org.apache.hadoop" module="hadoop-client" rev="2.0.0-cdh4.3.0"/>
    <exclude org="org.mockito" module="mockito-all"/>
    <exclude org="commons-beanutils" module="commons-beanutils"/>
    <exclude org="commons-beanutils" module="commons-beanutils-core"/>
    <override org="joda-time" module="joda-time" rev="2.3"/>
    <override org="org.joda" module="joda-convert" rev="1.4"/>
    <exclude org="org.jruby" module="jruby-complete"/>
    <exclude org="org.eclipse.jetty.orbit" module="javax.servlet"/>
    <exclude org="org.mortbay.jetty" module="servlet-api-2.5"/>
    <exclude org="org.mortbay.jetty" module="jsp-api-2.1"/>
    <exclude org="org.jboss.netty" module="netty"/>
    <exclude org="tomcat" module="jasper-compiler"/>
    <exclude org="tomcat" module="jasper-runtime"/>
    <exclude org="asm" module="asm"/>
    <exclude org="com.esotericsoftware.minlog" module="minlog"/>
    <exclude org="org.sonatype.sisu.inject" module="cglib"/>
  </dependencies>
