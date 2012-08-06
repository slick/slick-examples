package scala.slick.examples.lifted

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession

/**
 * Use various ways of reading data from an Invoker.
 */
object UseInvoker extends App {

  // A simple table with keys and values
  val T = new Table[(Int, String)]("T") {
    def k = column[Int]("KEY", O.PrimaryKey)
    def v = column[String]("VALUE")
    def * = k ~ v
  }

  Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver") withSession {

    // Create the table and insert some data
    T.ddl.create
    T.insertAll(1 -> "a", 2 -> "b", 3 -> "c", 4 -> "d", 5 -> "e")

    // Define a QueryTemplate for reading all key/value pairs up to a given key.
    // This is preferable to generating new queries in a parameterized def
    // because the SQL statement is only generated once and all parameters
    // are passed in via bind variables.
    val upTo = for {
      k <- Parameters[Int]
      t <- T if t.k <= k
      _ <- Query orderBy t.k
    } yield t

    println("List of k/v pairs up to 3 with .list")
    println("  " + upTo.list(3))

    println("IndexedSeq of k/v pairs up to 3 with .to")
    println("  " + upTo.to[IndexedSeq](3))

    println("Set of k/v pairs up to 3 with .to")
    println("  " + upTo.to[Set](3))

    println("Array of k/v pairs up to 3 with .to")
    println("  " + upTo.to[Array](3))

    println("All keys in an unboxed Array[Int]")
    val allKeys = T.map(_.k)
    println("  " + allKeys.to[Array]())
  }
}
