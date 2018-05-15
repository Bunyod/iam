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

//  private[es] var iamStagedUsersStatement: PreparedStatement = _
  private[es] var iamStatement: PreparedStatement = _

  // create the tables used by the read side processor
  def createTable(): Future[Done] = {
    session.executeCreateTable(
      """-- WARNING: Get rid of temp workaround. Also, status and role fields haven't got a proper type
        |CREATE TABLE IF NOT EXISTS iam.users(
        |username      text,
        |u_timestamp   timestamp,
        |u_status      int,
        |forename      text,
        |surname       text,
        |password      text,
        |role          int,
        |u_token       tuple<text,timestamp>,
        |PRIMARY KEY (username)
        |);
      """.stripMargin)

  }

  def createPreparedStatements: Future[Done] = {
    session.prepare(
      """INSERT INTO iam.users (username, u_timestamp, u_status, forename, surname, password, role, u_token)
        |VALUES (?, ?, ?, ?, ?, ?, ?, ?)""".stripMargin
    ).map {iamPreparedStatement =>
      iamStatement = iamPreparedStatement
      Done
    }

  }

  def stageUser(e: UserStagedEvt): Future[List[BoundStatement]] =
    session.underlying().map { _session =>
      logger.debug(s"Read-side: Staging user (Event: $e)")

      val iamBindStatement = iamStatement.bind()
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
      val iamBindStatement = iamStatement.bind()
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
         |SELECT * FROM iam.users
         | WHERE username = '$username'
       """.stripMargin
    ) map { _ map { row =>
      val username = row.getString("username")
      val timestamp = row.getTimestamp("u_timestamp")
      val forename = row.getString("forename")
      val surname = row.getString("surname")
      val statusInt = row.getInt("u_status")
      val status = statusInt match {
        case 0 => Staged
        case 1 => Active
        case -1 => Inactive
      }
//      val tupleValue = row.getTupleValue("u_token")
//      val token = tupleValue.getString(0)
//      val expiration = tupleValue.getTimestamp(1).toInstant
      logger.debug(s"User(username = $username, forename=$forename)")
      User(username, status, forename = Some(forename), surname = Some(surname), password = None)
    } }

//  def updateUser(user: User, token: UserToken): Future[Done] = {
//
//    val statement =
//      s"""UPDATE users
//            (u_status, forename, surname, password, role, u_token)
//             SET u_status = ?, forename = ?, surname = ?, password = ?, role = ?, u_token = ?
//             WHERE username = ${user.username}
//        """.stripMargin
//
//
//    session.underlying().flatMap { _session =>
//      val cluster = _session.getCluster
//      val tupleType = cluster.getMetadata.newTupleType(DataType.text(), DataType.timestamp())
//
//      val upVals: AnyRef = List(user.status.value,
//        user.forename.orNull,
//        user.surname.orNull,
//        user.password.orNull,
//        user.role.map(_.value).getOrElse(0),
//        tupleType.newValue(token.token, new Timestamp(token.expiration.getTime))
//      )
//
//      session.executeWrite(statement,upVals)
//    }
//  }

}
