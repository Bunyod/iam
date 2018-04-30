package mdpm
package iam.impl
package es

import java.util.Date
import scala.concurrent.{ExecutionContext, Future}
import akka.Done
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import com.datastax.driver.core._
import com.typesafe.scalalogging.StrictLogging
import mdpm.iam.api

class IamRepository(session: CassandraSession)(implicit ec: ExecutionContext) extends StrictLogging {

  private[es] var iamStatement: PreparedStatement = _

  // create the tables used by the read side processor
  def createTable(): Future[Done] = {
    session.executeCreateTable(
      """
        |-- User `username` has been staged on `timestamp`
        |-- with email token `stamp._1` which expires
        |-- on `stamp._2`.
        |
        |CREATE TABLE IF NOT EXISTS staged(
        |username    text,
        |"timestamp" timestamp,
        |"token"     tuple<text,timestamp>,
        |PRIMARY KEY (username)
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
        |INSERT INTO staged(username, "timestamp", "token")
        |VALUES (?, ?, ?)
      """.stripMargin
    ) map { iamPreparedStatement =>
      iamStatement = iamPreparedStatement
      Done
    }

  def stageUser(e: UserStaged): Future[List[BoundStatement]] =
    session.underlying().map { _session =>
      logger.debug(s"Read-side: Staging user (Event: $e)")

      val iamBindStatement = iamStatement.bind()
      val cluster = _session.getCluster
      val tupleType = cluster.getMetadata.newTupleType(DataType.text(), DataType.timestamp())

      iamBindStatement.setString("username", e.username)
      iamBindStatement.setTimestamp("timestamp", Date.from(e.token.expiration.minus(MailToken.DURATION)))
      iamBindStatement.setTupleValue("token", tupleType.newValue(e.token.uuid, Date.from(e.token.expiration)))

      List(iamBindStatement)
    }

  def getUser(username: api.EMail): Future[Option[User]] =
    session.selectOne(
      s"""
         |SELECT * FROM staged
         | WHERE username = '$username'
       """.stripMargin
    ) map { _ map { row =>
      val username = row.getString("username")
      val timestamp = row.getTimestamp("timestamp")
      val tupleValue = row.getTupleValue("token")
      val token = tupleValue.getString(0)
      val expiration = tupleValue.getTimestamp(1).toInstant
      logger.debug(s"User(username = $username, token = ($token, ${tsF.format(expiration)}))")
      User(username, Staged, password = Some(Left(MailToken(token, expiration))))
    } }

}
