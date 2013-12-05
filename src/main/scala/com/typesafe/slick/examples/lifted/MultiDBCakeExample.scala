package com.typesafe.slick.examples.lifted

import scala.slick.driver.JdbcProfile
import scala.slick.driver.H2Driver
import scala.slick.driver.SQLiteDriver

trait Profile {
  val profile: JdbcProfile
}

case class Picture(url: String, id: Option[Int] = None)

trait PictureComponent { this: Profile => //requires a Profile to be mixed in...
  import profile.simple._ //...to be able import profile.simple._ 

  class Pictures(tag: Tag) extends Table[Picture](tag, "PICTURES") {
    //                             ^ Table comes from the *current* profile
    def id = column[Option[Int]]("PIC_ID", O.PrimaryKey, O.AutoInc)
    def url = column[String]("PIC_URL", O.NotNull)

    def * = (url, id) <> (Picture.tupled, Picture.unapply)
  }

  val pictures = TableQuery[Pictures]

  private val picturesAutoInc = pictures returning pictures.map(_.id) into { case (p, id) => p.copy(id = id) }
  def insert(picture: Picture)(implicit session: Session): Picture = picturesAutoInc.insert(picture)
}

case class User(name: String, picture: Picture, id: Option[Int] = None)

trait UserComponent { this: Profile with PictureComponent => //requires Profile and Picture (see def insert)
  import profile.simple._

  class Users(tag: Tag) extends Table[(String, Int, Option[Int])](tag, "USERS") {
    def id = column[Option[Int]]("USER_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("USER_NAME", O.NotNull)
    def pictureId = column[Int]("PIC_ID", O.NotNull)
    def * = (name, pictureId, id)
  }
  val users = TableQuery[Users]

  private val usersAutoInc = users.map(u => (u.name, u.pictureId)) returning users.map(_.id) into {
    case (_, id) => id
  }

  def insert(user: User)(implicit session: Session): User = {
    val picture = if (user.picture.id.isEmpty) { //if no picture id...
      insert(user.picture) //...insert
    } else user.picture //else return current picture
    val id = usersAutoInc.insert(user.name, picture.id.get)
    user.copy(picture = picture, id = id)
  }
}

/**
 * The Data Access Layer contains all components and a profile
 */
class DAL(override val profile: JdbcProfile) extends UserComponent with PictureComponent with Profile {
  import profile.simple._
  def create(implicit session: Session): Unit = {
    (users.ddl ++ pictures.ddl).create //helper method to create all tables
  }
}

/**
 * Run SLICK code with multiple DBMSs using the Cake pattern.
 * Typically this technique can be used to have different DBMS 
 * in production and test (other scenarios are possible as well)
 */
object MultiDBCakeExample {
  // We only need the DB import outside the DAL
  import scala.slick.jdbc.JdbcBackend.Database

  def run(name: String, dal: DAL, db: Database) {
    import dal._
    import dal.profile.simple._

    println("Running test against " + name)
    db withSession { implicit session: Session =>
      dal.create

      //creating our default picture
      val defaultPic = insert(Picture("http://pics/default"))
      println("  Inserted picture: " + defaultPic)

      //inserting users
      val (user1, user2, user3) = (User("name1", defaultPic), User("name2", Picture("http://pics/2")), User("name3", defaultPic))
      println("  Inserted user: " + insert(user1))
      println("  Inserted user: " + insert(user2))
      println("  Inserted user: " + insert(user3))
      println("  All pictures: " + pictures.list)
      println("  All users : " + users.list)
    }
  }

  def main(args: Array[String]) {
    run("H2", new DAL(H2Driver),
      Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver"))
    run("SQLite", new DAL(SQLiteDriver),
      Database.forURL("jdbc:sqlite::memory:", driver = "org.sqlite.JDBC"))
  }
}
