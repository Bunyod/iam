package services

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

import play.api.db.DBApi

import javax.inject._

import models.User
import util.concurrent.DatabaseExecutionContext

trait UserRepository {
  def add(u: User): Future[Either[String, Long]]

  def list: Future[Seq[User]]
}

@Singleton
class DefaultUserRepository @Inject()(dbApi: DBApi)(implicit ec: DatabaseExecutionContext) extends UserRepository {
  import anorm.SqlParser.str
  import anorm.{Success => _, _}

  private val db = dbApi.database("default")

  def add(u: User) = Future { db.withConnection { implicit connection =>
    Try {
      SQL"""
        INSERT INTO User(email, password, forename, surname, phone, role)
        VALUES (${u.email}, ${u.password}, ${u.forename}, ${u.surname}, ${u.phone}, ${u.role})
      """.executeInsert(SqlParser.scalar[Long].single)
    } match {
      case Success(id) => Right(id)
      case Failure(e)  => Left(e.getMessage)
    }
  } }(ec)

  def list: Future[Seq[User]] = Future { db.withConnection { implicit connection =>
    Try {
      SQL"""
        SELECT email, forename, surname, phone, role
          FROM User
      """.executeQuery.as(
        (str("email") ~ str("forename") ~ str("surname") ~ str("phone") ~ str("role") map {
          case email ~ forename ~ surname ~ phone ~ role => User(email, "n/a", forename, surname, phone, role)
        }).*
      )
    } match {
      case Success(u) => u
      case Failure(e) => Seq.empty
    }
  } }(ec)

}
