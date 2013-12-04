organization := "com.typesafe.slick"

name := "slick-examples"

version := "2.0.0-M3"

scalaVersion := "2.10.3"

scalacOptions += "-deprecation"

// scala-compiler is declared as an optional dependency by Slick.
// You need to add it explicitly to your own project if you want
// to use the direct embedding (as in SimpleExample.scala here).
libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-compiler" % _)

libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "2.0.0-M3",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.h2database" % "h2" % "1.3.166",
  "org.xerial" % "sqlite-jdbc" % "3.7.2"
/*
  "org.apache.derby" % "derby" % "10.9.1.0",
  "org.hsqldb" % "hsqldb" % "2.2.8",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "mysql" % "mysql-connector-java" % "5.1.23",
  "net.sourceforge.jtds" % "jtds" % "1.2.4" % "test"
*/
)
