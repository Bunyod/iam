package mdpm
package iam.impl

import scala.concurrent.ExecutionContext
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import org.slf4j.LoggerFactory
import play.api.libs.mailer._
import mdpm.iam.api

/** Implementation of the IAM µS. */
class IamServiceImpl(
  registry: PersistentEntityRegistry,
  mailer:   MailerClient
)(implicit ec: ExecutionContext) extends api.IamService {

  private val logger = LoggerFactory.getLogger(classOf[IamServiceImpl])

  //********************************************************************************************************************
  // User registration
  //********************************************************************************************************************

  override val signup: ServiceCall[api.Signup, api.Result] = ServiceCall { request =>
    registry.refFor[IamEntity](request.username).ask(StageUser(request.username)) map { token =>

      //      result.`type` match {
      //        case Info => createToken(request.username) map { token =>
      //          sendSignup(request.username, token)
      //
      //        }
      //        case Warn => ???
      //        case _    => ()
      //      }

      //api.Result(Info, Some(s"User '${username}' was successfully staged on ${tsF.format(state.timestamp)}."))
      //api.Result(Warn, Some(s"User '${username}' has already been staged at ${tsF.format(ts)}."), Some("E-mail offering to login/recover password has been sent."))
      ???
    }
  }

  override def register(token: String): ServiceCall[api.Register, api.Result] = ???

}

