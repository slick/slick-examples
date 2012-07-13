organization := "com.typesafe"

name := "slick-examples"

version := "0.10.0"

scalaVersion := "2.10.0-M4"

scalacOptions += "-deprecation"

libraryDependencies ++= List(
  "com.typesafe" % "slick_2.10.0-M4" % "0.10.0-SNAPSHOT",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.h2database" % "h2" % "1.3.166",
  "org.xerial" % "sqlite-jdbc" % "3.6.20"
/*
  "org.apache.derby" % "derby" % "10.6.1.0",
  "org.hsqldb" % "hsqldb" % "2.0.0",
  "postgresql" % "postgresql" % "8.4-701.jdbc4",
  "mysql" % "mysql-connector-java" % "5.1.13"
*/
)
