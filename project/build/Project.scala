import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info)
{
  /*********** Options ***********/
  override def compileOptions = Deprecation :: super.compileOptions.toList

  val useJDBC4 = try { classOf[java.sql.DatabaseMetaData].getMethod("getClientInfoProperties"); true }
    catch { case _:NoSuchMethodException => false }

  /*********** Dependencies ***********/
  val scalaQuery = "org.scalaquery" %% "scalaquery" % "0.9.1"
  val h2 = "com.h2database" % "h2" % "1.2.140"
  val sqlite = "org.xerial" % "sqlite-jdbc" % "3.6.20"
  //val derby = "org.apache.derby" % "derby" % "10.6.1.0"
  //val hsqldb = "org.hsqldb" % "hsqldb" % "2.0.0"
  //val postgresql = "postgresql" % "postgresql" % (if(useJDBC4) "8.4-701.jdbc4" else "8.4-701.jdbc3")
  //val mysql = "mysql" % "mysql-connector-java" % "5.1.13"
  //val junitInterface = "com.novocode" % "junit-interface" % "0.5"
}
