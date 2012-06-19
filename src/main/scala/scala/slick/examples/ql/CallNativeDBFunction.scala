package scala.slick.examples.ql

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession
import java.sql.Date

/**
 * This example shows how to lift a native database function to ScalaQuery's
 * query language.
 */
object CallNativeDBFunction {
  def main(args: Array[String]) {

    val SalesPerDay = new Table[(Date, Int)]("SALES_PER_DAY") {
      def day = column[Date]("DAY", O.PrimaryKey)
      def count = column[Int]("COUNT")
      def * = day ~ count
    }

    // H2 has a day_of_week() function which extracts the day of week from a
    // timestamp value. We lift it to a ScalaQuery SimpleFunction which
    // returns a Column[Int]. For simplicity's sake, SimpleFunctions take a
    // Seq of Columns of any type
    val dayOfWeek = SimpleFunction[Int]("day_of_week")

    // If you want to check the parameter types, you can write a wrapper
    // function like this:
    def dayOfWeek2(c: Column[Date]) = dayOfWeek(Seq(c))

    Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver") withSession {
      SalesPerDay.ddl.create
      SalesPerDay.insertAll(
        (Date.valueOf("2011-04-01"), 3),
        (Date.valueOf("2011-04-02"), 8),
        (Date.valueOf("2011-04-03"), 0),
        (Date.valueOf("2011-04-04"), 2),
        (Date.valueOf("2011-04-05"), 1),
        (Date.valueOf("2011-04-06"), 1),
        (Date.valueOf("2011-04-07"), 2),
        (Date.valueOf("2011-04-08"), 5),
        (Date.valueOf("2011-04-09"), 4),
        (Date.valueOf("2011-04-10"), 0),
        (Date.valueOf("2011-04-11"), 2)
      )

      /*
      // Use the lifted function in a query to group by day of week
      val q1 = for {
        dow ~ count <- SalesPerDay.map(s => dayOfWeek2(s.day) ~ s.count)
        _ <- Query groupBy dow
      } yield dow ~ count.sum.get

      println("Day of week (1 = Sunday) -> Sales:")
      q1.foreach { case (dow, sum) => println("  " + dow + "\t->\t" + sum) }
      */
    }
  }
}
