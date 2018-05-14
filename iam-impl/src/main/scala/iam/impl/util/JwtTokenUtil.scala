package mdpm
package iam.impl
package util

import java.util.Date

import com.lightbend.lagom.scaladsl.api.transport.Forbidden
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import mdpm.iam.impl.es.StageUser
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim, JwtJson}
import play.api.libs.json.Json

import scala.util.{Failure, Success}

object JwtTokenUtil extends StrictLogging {

  val secret = ConfigFactory.load().getString("jwt.secret")
  val authExpiration = ConfigFactory.load().getInt("jwt.token.auth.expirationInSeconds")
  val algorithm = JwtAlgorithm.HS512

  def generateAuthToken(stageUser: StageUser): String = {
    val jsUser = Json.toJson(stageUser).toString()
    val authClaim = JwtClaim(jsUser)
      .expiresIn(authExpiration) //TODO use different expiration time
      .issuedAt(new Date().getTime)

    JwtJson.encode(authClaim, secret, algorithm)
  }

  def generateUserToken(user: User): String = {
    val userInfo = UserInfo(
      username = user.username,
      forename = user.forename,
      surname = user.surname
    )

    val authClaim = JwtClaim(Json.toJson(userInfo).toString())
      .expiresIn(authExpiration)
      .issuedAt(new Date().getTime)

    val token = Jwt.encode(authClaim, secret, algorithm)

    token
  }

  def decodeStageUserToken(token: String): Option[StageUser] = {
    val jsonTokenContent = JwtJson.decode(token, secret, Seq(algorithm))
    jsonTokenContent match {
      case Success(jwtJson) =>
        Json.parse(jwtJson.content).validate[StageUser].asOpt
      case Failure(ex) =>
        logger.error(s"Error occured while decoding: $ex")
        None
    }
  }

  def decodeToken(token: String): UserInfo = {
    val jsonTokenContent = JwtJson.decode(token, secret, Seq(algorithm))
    jsonTokenContent match {
      case Success(json) => Json.parse(json.content).as[UserInfo]
      case Failure(_) => throw Forbidden(s"Unable to decode token")
    }
  }

}

