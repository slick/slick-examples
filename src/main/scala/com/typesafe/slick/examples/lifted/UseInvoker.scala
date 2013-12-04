package com.typesafe.slick.examples.lifted

import scala.slick.driver.H2Driver.simple._

/**
 * Use various ways of reading data from an Invoker.
 */
object UseInvoker extends App {

  // A simple table with keys and values
  class T(tag: Tag) extends Table[(Int, String)](tag, "T") {
    def k = column[Int]("KEY", O.PrimaryKey)
    def v = column[String]("VALUE")
    def * = (k, v)
  }
  val ts = TableQuery[T]

  Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver") withSession { implicit session =>

    // Create the table and insert some data
    ts.ddl.create
    ts.insertAll(1 -> "a", 2 -> "b", 3 -> "c", 4 -> "d", 5 -> "e")

    // Define a QueryTemplate for reading all key/value pairs up to a given key.
    // This is preferable to generating new queries in a parameterized def
    // because the SQL statement is only generated once and all parameters
    // are passed in via bind variables.
    val upTo = Parameters[Int].flatMap { k =>
      ts.filter(_.k <= k).sortBy(_.k)
    }

    println("List of k/v pairs up to 3 with .list")
    println("  " + upTo(3).list)

    println("IndexedSeq of k/v pairs up to 3 with .to")
    println("  " + upTo(3).to[IndexedSeq])

    println("Set of k/v pairs up to 3 with .to")
    println("  " + upTo(3).to[Set])

    println("Array of k/v pairs up to 3 with .to")
    println("  " + upTo(3).to[Array])

    println("All keys in an unboxed Array[Int]")
    val allKeys = ts.map(_.k)
    println("  " + allKeys.to[Array]())
  }
}
