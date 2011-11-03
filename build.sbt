organization := "org.scalaquery"

name := "examples"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.1"

scalacOptions += "-deprecation"

libraryDependencies += "org.scalaquery" % "scalaquery_2.9.0-1" % "0.9.5" withSources()

libraryDependencies += "com.h2database" % "h2" % "1.2.140"

libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.6.20"
/*
libraryDependencies += "org.apache.derby" % "derby" % "10.6.1.0"
libraryDependencies += "org.hsqldb" % "hsqldb" % "2.0.0"
libraryDependencies += {
  val useJDBC4 = try { classOf[java.sql.DatabaseMetaData].getMethod("getClientInfoProperties"); true }
    catch { case _:NoSuchMethodException => false }
  "postgresql" % "postgresql" % (if(useJDBC4) "8.4-701.jdbc4" else "8.4-701.jdbc3")
}
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.13"
libraryDependencies += "com.novocode" % "junit-interface" % "0.5"
*/
