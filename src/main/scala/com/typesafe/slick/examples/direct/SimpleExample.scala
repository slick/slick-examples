package com.typesafe.slick.examples.direct

import scala.slick.driver.H2Driver
import scala.slick.driver.H2Driver.simple.{Session,Database}
import scala.slick.direct._
import scala.slick.direct.AnnotationMapper._
import scala.reflect.runtime.universe
import scala.slick.jdbc.StaticQuery.interpolation

// define classes
@table("COFFEES") case class Coffee(
  @column("COF_NAME") name: String,
  @column("PRICE") price: Double
)

/**
 * Example queries using the experimental direct embedding prototype
 */
class SimpleExampleClass{ // side note: direct embedding not supported in singleton object until next Slick release. 
  // convenience functions
  def query[T]( q:QueryableValue[T] )(implicit session:Session) : T = backend.result(q, session)
  def query[T]( q:Queryable[T] )(implicit session:Session) : Vector[T] = backend.result(q, session)

  // database queries specified using direct embedding
  val coffees = Queryable[Coffee]
  val priceAbove3 = coffees.filter(_.price > 3.0).map(_.name)
  val samePrice = for( c1 <- coffees; c2 <- coffees; if c1.price == c2.price ) yield (c1.name,c2.name)
  
  // some dummy data
  val coffees_data = Vector(
    ("Colombian",          2),
    ("French_Roast",       2),
    ("Espresso",           5),
    ("Colombian_Decaf",    4),
    ("French_Roast_Decaf", 5)
  )

  // direct embedding backend (AnnotationMapper evaluates the @table and @column annotations. Use custom mapper for other mappings) 
  val backend = new SlickBackend(H2Driver, AnnotationMapper)

  Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver") withSession { implicit session:Session =>
    // insert using SQL (currently not supported by direct embedding)
    import slick.jdbc.StaticQuery.interpolation
    sqlu"create table COFFEES(COF_NAME varchar(255), PRICE DOUBLE)".execute
    (for {
      (name, sales) <- coffees_data
    } yield sqlu"insert into COFFEES values ($name, $sales)".first).sum

    // execute queries
    Seq(
      coffees,
      coffees.filter(_.price > 3.0).map(_.name), // inline query
      priceAbove3,
      samePrice
    ).foreach( q => println(query(q)) )

    println(query(priceAbove3.length))
  }
}
object SimpleExample extends SimpleExampleClass with App{
  
}