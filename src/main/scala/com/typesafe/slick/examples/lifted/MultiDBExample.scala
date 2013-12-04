package com.typesafe.slick.examples.lifted

import scala.slick.driver.{JdbcProfile, H2Driver, SQLiteDriver}
import slick.ast.Dump

/**
 * All database code goes into the DAO (data access object) class which is
 * parameterized by a SLICK driver that implements JdbcProfile.
 */
class DAO(val driver: JdbcProfile) {
  // Import the query language features from the driver
  import driver.simple._

  class Props(tag: Tag) extends Table[(String, String)](tag, "properties") {
    def key = column[String]("key", O.PrimaryKey)
    def value = column[String]("value")
    def * = (key, value)
  }
  val props = TableQuery[Props]

  def create(implicit session: Session) =
    props.ddl.create

  def insert(k: String, v: String)(implicit session: Session) =
    props.insert(k, v)

  def get(k: String)(implicit session: Session): Option[String] =
    (for(p <- props if p.key === k) yield p.value).firstOption

  def getFirst[M, U](q: Query[M, U])(implicit s: Session) = q.first
}

/**
 * Common functionality that needs to work with types from the DAO
 * but in a DAO-independent way. This is done with path-dependent types.
 */
class DAOHelper(val d: DAO) {
  import d.driver.simple._

  def restrictKey(s: String, q: Query[d.Props, (String, String)]):
      Query[d.Props, (String, String)] = q.filter(_.key === s)
}

/**
 * Run SLICK code with multiple DBMSs.
 */
object MultiDBExample {
  // We only need the DB/session imports outside the DAO
  import scala.slick.jdbc.JdbcBackend.{Database, Session}

  def run(name: String, dao: DAO, db: Database) {
    println("Running test against " + name)
    db withSession { implicit session: Session =>
      dao.create
      dao.insert("foo", "bar")
      dao.insert("a", "b")
      println("  Value for key 'foo': " + dao.get("foo"))
      println("  Value for key 'baz': " + dao.get("baz"))
      val h = new DAOHelper(dao)
      println("  Using the helper: " +
        h.d.getFirst(h.restrictKey("foo", h.d.props)))
    }
  }

  def main(args: Array[String]) {
    run("H2", new DAO(H2Driver),
      Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver"))
    run("SQLite", new DAO(SQLiteDriver),
      Database.forURL("jdbc:sqlite::memory:", driver = "org.sqlite.JDBC"))
  }
}
