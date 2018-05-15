package mdpm
package iam.impl
package svc

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import com.typesafe.scalalogging.StrictLogging
import mdpm.iam.api
import mdpm.iam.api.Result.{Error, Info}
import mdpm.iam.api.{Register, Result}
import mdpm.iam.impl.es._
import mdpm.iam.impl.util.JwtTokenUtil
import mdpm.iam.impl.util.SimpleAuth._
import mdpm.iam.impl.util.ValidationUtil._
import play.api.libs.json.Json
import play.api.libs.mailer._

import scala.concurrent.ExecutionContext

/** Implementation of the IAM µS. */
class IamServiceImpl(
  registry: PersistentEntityRegistry,
  mailer  : MailerClient,
  iamRepository: IamRepository
)(implicit ec: ExecutionContext) extends api.IamService with StrictLogging {

  override val signup: ServiceCall[api.Signup, api.Result] = ServiceCall { request =>
    registry.refFor[IamEntity](request.username).ask(StageUser(request.username)) map { token =>
//      logger.debug(s"User '${request.username}' successfully staged on ${tsF.format(token.expiration.minus(MailToken.DURATION))} with token '${token.uuid}'.")
      api.Result(Info, Some(s"User '${request.username}' successfully staged."), Some(token.token))
    }
  }

  override val signup1: ServiceCall[api.Signup, api.Result] = ServiceCall { request =>
    val email = request.username
    require(isValidEmail(email)) //TODO add validation to check existing email

    registry.refFor[IamEntity](email).ask(StageUser(email)) map { stagedUser =>
      logger.debug(s"User '${request.username}' successfully staged on ${stagedUser.issuedAt} with token '${stagedUser.token}'.")
      //TODO sent email with unique url in the email body, like: https://iam.com/api/user?token=jwt
//      mailer.send(email)
      api.Result(Info, Some(s"User '${request.username}' successfully staged"), Some(stagedUser.token))
    }
  }

  /**
    * Register a new user.
    *
    * @example
    * {{{
    * curl -H "Content-Type: application/json"\
    *      -X POST\
    *      -d '{"forename": "Normen", "surname" : "Müller", "password" : "???" }'\
    *      http://localhost:9000/register
    * }}}
    * @return A [[Result]] of type [[Result.Info]], [[Result.Warn]], or [[Result.Error]].
    */
  override def register(token: String): ServiceCall[Register, Result] = ServiceCall { request =>
    val userTokenOpt = JwtTokenUtil.decodeStageUserToken(token)
    logger.info(s"DecodedToken=$userTokenOpt")
    require(isValidToken(token) && userTokenOpt.isDefined)
    val username = userTokenOpt.get.username
//    val user = User(username, Active, forename = Some(request.forename), surname = Some(request.surname), password = Some(Right(request.password)))
//    val newToken = JwtTokenUtil.generateUserToken(user)
//    val userToken = UserToken(newToken)
//    iamRepository.updateUser(user, userToken).map { _ =>
//      api.Result(Info, Some(s"User '$username' successfully registered"), Some(newToken))
//    }
    val registerUser = RegisterUser(request.forename, request.surname, request.password, username)
    val ref = registry.refFor[IamEntity](username)
    ref.ask(registerUser) map { user =>
      api.Result(Info, Some(s"User '$username' successfully registered"), Some(user.token))
    }

  }

  /**
    * Get user info.
    *
    * @example
    * {{{
    * curl -H "Content-Type: application/json"\
    *      -X GET\
    *      http://localhost:9000/api/user
    * }}}
    * @return A [[Result]] of type [[Result.Info]], [[Result.Warn]], or [[Result.Error]].
    */
  override def getUserInfo() = authenticated { (tokenContent, _) =>
    ServerServiceCall { _ =>
//      val ref = registry.refFor[IamEntity](tokenContent.username)
//      ref.ask(GetUserInfo).map { userInfo =>
//        api.Result(Info, Some(s"User successfully authorized"), Some(userInfo.toString))
//      }
      iamRepository.getUser(tokenContent.username).map {
        case None =>
          api.Result(Error, Some(s"User not found"), None)
        case Some(user) =>
          val userInfoStr = Json.stringify(Json.toJson(user.toUserInfo))
          api.Result(Info, Some(s"User successfully authorized"), Some(userInfoStr))
      }

    }
  }

}

