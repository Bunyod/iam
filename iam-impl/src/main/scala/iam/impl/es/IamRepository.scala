package mdpm
package iam.impl
package es

import java.util.Date
import scala.concurrent.{ExecutionContext, Future}
import akka.Done
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import com.datastax.driver.core.{BoundStatement, PreparedStatement}
import org.slf4j.LoggerFactory
import mdpm.iam.api

class IamRepository(session: CassandraSession)(implicit ec: ExecutionContext) {

  private val logger = LoggerFactory.getLogger(classOf[IamRepository])

  private[es] var iamStatement: PreparedStatement = _

  // create the tables used by the read side processor
  // TODO Add `mailtoken tuple<string,timestamp>`
  def createTable(): Future[Done] = {
    session.executeCreateTable(
      """
        |CREATE TABLE IF NOT EXISTS usertable(
        |username  text PRIMARY KEY        ,
        |timestamp timestamp               ,
        |status    int
        |);
      """.stripMargin)

    //session.executeCreateTable(
    //  """
    //    |CREATE INDEX IF NOT EXISTS
    //    |username_index ON usertable (username);
    //  """.stripMargin)
  }

  def createPreparedStatements: Future[Done] =
    session.prepare(
      """
        |INSERT INTO usertable(username, timestamp, status)
        |VALUES (?, ?, ?)
      """.stripMargin
    ) map { iamPreparedStatement =>
      iamStatement = iamPreparedStatement
      Done
    }

  def stageUser(e: UserStaged): Future[List[BoundStatement]] = {
    logger.debug(s"Read-side: Staging user (Event: $e)")

    val iamBindStatement = iamStatement.bind()

    iamBindStatement.setString("username", e.username)
    iamBindStatement.setTimestamp("timestamp", Date.from(e.timestamp))
    iamBindStatement.setInt("status", 0)

    Future.successful(List(iamBindStatement))
  }

  def getUser(username: api.EMail): Future[Option[User]] =
    session.selectOne(
      s"SELECT * FROM usertable WHERE username = '$username'"
    ) map { _ map { row =>
        val username = row.getString("username")
        val timestamp = row.getDate("timestamp")
        val status = row.getInt("status") match {
          case 0 => Staged
          case 1 => Active
          case _ => Inactive
        }
        User(username, status)
      }
    }

}
