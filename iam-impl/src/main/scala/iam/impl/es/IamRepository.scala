package mdpm
package iam.impl
package es

import java.sql.Timestamp

import scala.concurrent.{ExecutionContext, Future}
import akka.Done
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import com.datastax.driver.core._
import com.typesafe.scalalogging.StrictLogging
import mdpm.iam.api

class IamRepository(session: CassandraSession)(implicit ec: ExecutionContext) extends StrictLogging {

  private[es] var iamStagedUsersStatement: PreparedStatement = _
  private[es] var iamUsersStatement: PreparedStatement = _

  // create the tables used by the read side processor
  def createTable(): Future[Done] = {
    session.executeCreateTable(
      """-- User `username` has been staged on `timestamp`
        |-- with email token `stamp._1` which expires
        |-- on `stamp._2`.
        |
        |CREATE TABLE IF NOT EXISTS iam.staged(
        |username    text,
        |u_timestamp timestamp,
        |u_token     tuple<text,timestamp>,
        |PRIMARY KEY (username)
        |);
        |
        |-- WARNING: Get rid of temp workaround. Also, status and role fields haven't got a proper type
        |CREATE TABLE IF NOT EXISTS iam.users(
        |username    text,
        |u_timestamp   timestamp,
        |u_status      int,
        |forename    text,
        |surname     text,
        |password    text,
        |role        int,
        |u_token     tuple<text,timestamp>,
        |PRIMARY KEY (username)
        |);
      """.stripMargin)

  }


  def createPreparedStatements: Future[Done] = {
    for {
      insertStagedUser <- session.prepare(
        """INSERT INTO
          |staged(username, u_timestamp, u_token)
          |VALUES (?, ?, ?)""".stripMargin
      )
      updateUser <- session.prepare(
        """INSERT INTO users (username, u_timestamp, u_status, forename, surname, password, role, u_token)
          |VALUES (?, ?, ?, ?, ?, ?, ?, ?)""".stripMargin
      )
    } yield {
      iamStagedUsersStatement = insertStagedUser
      iamUsersStatement= updateUser
      Done
    }
  }

  def stageUser(e: UserStagedEvt): Future[List[BoundStatement]] =
    session.underlying().map { _session =>
      logger.debug(s"Read-side: Staging user (Event: $e)")

      val iamBindStatement = iamStagedUsersStatement.bind()
      val cluster = _session.getCluster
      val tupleType = cluster.getMetadata.newTupleType(DataType.text(), DataType.timestamp())

      iamBindStatement.setString("username", e.username)
      iamBindStatement.setTimestamp("u_timestamp", e.token.issuedAt)
      iamBindStatement.setTupleValue("u_token", tupleType.newValue(e.token.token, e.token.expiration))

      List(iamBindStatement)
    }

  def registerUser(regUser: UserRegisteredEvt): Future[List[BoundStatement]] =
    session.underlying().map { _session =>
      logger.debug(s"Read-side: Registering user (Event: $regUser)")

      val password = regUser.user.password.map {
        case Right(pwd) => pwd
        case Left(mailToken) => mailToken.toString
      }
      val iamBindStatement = iamUsersStatement.bind()
      val cluster = _session.getCluster
      val tupleType = cluster.getMetadata.newTupleType(DataType.text(), DataType.timestamp())
      iamBindStatement.setString("username", regUser.user.username)
      iamBindStatement.setTimestamp("u_timestamp", regUser.token.issuedAt)
      iamBindStatement.setInt("u_status", regUser.user.status.value)
      iamBindStatement.setString("forename", regUser.user.forename.orNull)
      iamBindStatement.setString("surname", regUser.user.surname.orNull)
      iamBindStatement.setString("password", password.orNull)
      iamBindStatement.setInt("role", regUser.user.role.map(_.value).getOrElse(0))
      iamBindStatement.setTupleValue("u_token", tupleType.newValue(regUser.token, regUser.token.expiration))

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
      val timestamp = row.getTimestamp("u_timestamp")
      val tupleValue = row.getTupleValue("u_token")
      val token = tupleValue.getString(0)
      val expiration = tupleValue.getTimestamp(1).toInstant
      logger.debug(s"User(username = $username, token = ($token, ${tsF.format(expiration)}))")
      User(username, Staged, password = None)
    } }

  def updateUser(user: User, token: UserToken): Future[Done] = {

    val statement =
      s"""UPDATE users
            (u_status, forename, surname, password, role, u_token)
             SET u_status = ?, forename = ?, surname = ?, password = ?, role = ?, u_token = ?
             WHERE username = ${user.username}
        """.stripMargin


    session.underlying().flatMap { _session =>
      val cluster = _session.getCluster
      val tupleType = cluster.getMetadata.newTupleType(DataType.text(), DataType.timestamp())

      val upVals: AnyRef = List(user.status.value,
        user.forename.orNull,
        user.surname.orNull,
        user.password.orNull,
        user.role.map(_.value).getOrElse(0),
        tupleType.newValue(token.token, new Timestamp(token.expiration.getTime))
      )

      session.executeWrite(statement,upVals)
    }
  }

}
