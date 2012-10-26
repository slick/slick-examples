package scala.slick.examples.lifted

import scala.slick.driver.ExtendedProfile
import scala.slick.session.Session
import scala.slick.driver.H2Driver
import scala.slick.driver.SQLiteDriver

trait Profile {
  val profile: ExtendedProfile
}

case class Picture(url: String, id: Option[Int] = None)

trait PictureComponent { this: Profile => //requires a Profile to be mixed in...
  import profile.simple._ //...to be able import profile.simple._ 

  object Pictures extends Table[(String, Option[Int])]("PICTURES") {
    //                    ^ Table comes from the *current* profile
    def id = column[Option[Int]]("PIC_ID", O.PrimaryKey, O.AutoInc)
    def url = column[String]("PIC_URL", O.NotNull)

    def * = url ~ id

    val autoInc = url returning id into { case (url, id) => Picture(url, id) }

    def insert(picture: Picture)(implicit session: Session): Picture = {
      autoInc.insert(picture.url)
    }
  }
}

case class User(name: String, picture: Picture, id: Option[Int] = None)

trait UserComponent { this: Profile with PictureComponent => //requires Profile and Picture (see def insert)
  import profile.simple._

  object Users extends Table[(String, Int, Option[Int])]("USERS") {
    def id = column[Option[Int]]("USER_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("USER_NAME", O.NotNull)
    def pictureId = column[Int]("PIC_ID", O.NotNull)
    def * = name ~ pictureId ~ id

    private def autoInc(implicit session: Session) = name ~ pictureId returning id into {
      case (_, id) => id
    }

    def insert(user: User)(implicit session: Session): User = {
      val picture = if (user.picture.id.isEmpty) { //if no picture id...
        Pictures.insert(user.picture) //...insert
      } else user.picture //else return current picture

      val id = autoInc.insert(user.name, picture.id.get)
      user.copy(picture = picture, id = id)
    }
  }
}

/**
 * The Data Access Layer contains all components and a profile
 */
class DAL(override val profile: ExtendedProfile) extends UserComponent with PictureComponent with Profile {
  import profile.simple._
  def create(implicit session: Session): Unit = {
    (Users.ddl ++ Pictures.ddl).create //helper method to create all tables
  }
}

/**
 * Run SLICK code with multiple DBMSs using the Cake pattern.
 * Typically this technique can be used to have different DBMS 
 * in production and test (other scenarios are possible as well)
 */
object MultiDBCakeExample {
  // We only need the DB/session imports outside the DAL
  import scala.slick.session.{ Database, Session }

  def run(name: String, dal: DAL, db: Database) {
    import dal._
    import dal.profile.simple._

    println("Running test against " + name)
    db withSession { session: Session =>
      implicit val implicitSession = session
      dal.create

      //creating our default picture
      val defaultPic = Pictures.insert(Picture("http://pics/default"))
      println("  Inserted picture: " + defaultPic)

      //inserting users
      val (user1, user2, user3) = (User("name1", defaultPic), User("name2", Picture("http://pics/2")), User("name3", defaultPic))
      println("  Inserted user: " + Users.insert(user1))
      println("  Inserted user: " + Users.insert(user2))
      println("  Inserted user: " + Users.insert(user3))
      println("  All pictures: " + Query(Pictures).list)
      println("  All users : " + Query(Users).list)
    }
  }

  def main(args: Array[String]) {
    run("H2", new DAL(H2Driver),
      Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver"))
    run("SQLite", new DAL(SQLiteDriver),
      Database.forURL("jdbc:sqlite::memory:", driver = "org.sqlite.JDBC"))
  }
}
