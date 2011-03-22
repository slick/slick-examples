package org.scalaquery.examples

import org.scalaquery.session._
import org.scalaquery.ql._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.extended.{ExtendedProfile, H2Driver, SQLiteDriver}
import org.scalaquery.ql.extended.{ExtendedTable => Table}

/**
 * Run ScalaQuery code with multiple DBMSs.
 */
object MultiDBExample {

  // All database code goes into the DAO (data access object) class which is
  // parameterized by a ScalaQuery driver that implements ExtendedProfile
  class DAO(driver: ExtendedProfile) {
    // Import the implicit conversions and values provided by the driver
    import driver.Implicit._

    val Props = new Table[(String, String)]("properties") {
      def key = column[String]("key", O.PrimaryKey)
      def value = column[String]("value")
      def * = key ~ value
    }

    def create(implicit session: Session) =
      Props.ddl.create

    def insert(k: String, v: String)(implicit session: Session) =
      Props.insert(k, v)

    def get(k: String)(implicit session: Session): Option[String] =
      (for(p <- Props if p.key === k) yield p.value).firstOption
  }

  def run(name: String, dao: DAO, db: Database) {
    println("Running test against " + name)
    db withSession { session: Session =>
      implicit val implicitSession = session
      dao.create
      dao.insert("foo", "bar")
      println("  Value for key 'foo': " + dao.get("foo"))
      println("  Value for key 'baz': " + dao.get("baz"))
    }
  }

  def main(args: Array[String]) {
    run("H2", new DAO(H2Driver),
      Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver"))
    run("SQLite", new DAO(SQLiteDriver),
      Database.forURL("jdbc:sqlite::memory:", driver = "org.sqlite.JDBC"))
  }
}
