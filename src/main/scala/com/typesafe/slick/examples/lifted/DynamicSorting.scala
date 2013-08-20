package com.typesafe.slick.examples.lifted
import scala.slick.driver.H2Driver.simple._

object DynamicSorting extends App {
  object Suppliers extends Table[(Int, String, String, String, String, String)]("SUPPLIERS") {
    def id = column[Int]("SUP_ID", O.PrimaryKey) // This is the primary key column
    def name = column[String]("SUP_NAME")
    def street = column[String]("STREET")
    def city = column[String]("CITY")
    def state = column[String]("STATE")
    def zip = column[String]("ZIP")
    def * = id ~ name ~ street ~ city ~ state ~ zip
  }

  Database.forURL("jdbc:h2:mem:test1", driver="org.h2.Driver") withSession { implicit session:Session =>
    Suppliers.ddl.create
    Suppliers.insert(101, "Acme, Inc.",      "99 Market Street", "Groundsville", "CA", "95199")
    Suppliers.insert( 49, "Superior Coffee", "1 Party Place",    "Mendocino",    "CA", "95460")
    Suppliers.insert(150, "The High Ground", "100 Coffee Lane",  "Meadows",      "CA", "93966")

    // split string into useful pieces
    def sortKeysFromString(sortString: String) = sortString.split(',').toList.map( _.split('.').map(_.toUpperCase).toList )

    // apply sort keys as sortBy calls in reverse order
    def sortKey[T](table : Table[T], sortKeys: List[Seq[String]]) : Query[_,T] = sortKeys match {
      case key :: tail => 
        sortKey( table, tail ).sortBy( _ =>
          key match {
            case name :: "ASC" :: Nil =>  table.column[String](name).asc
            case name :: Nil =>           table.column[String](name).asc
            case name :: "DESC" :: Nil => table.column[String](name).desc
            case o => throw new Exception("invalid sorting key: "+o)
          }
        )
      case Nil => Query(table)
    }
    println(
      Query(Suppliers).flatMap(
        table => sortKey(table, sortKeysFromString("street.desc,city.desc"))
      ).selectStatement
    )
  }
}
