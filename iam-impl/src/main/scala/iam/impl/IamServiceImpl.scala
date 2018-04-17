package mdpm
package iam.impl

import scala.concurrent.ExecutionContext
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import org.slf4j.LoggerFactory
import play.api.libs.mailer._
import mdpm.iam.api
import mdpm.iam.api.Result.Info

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
    registry.refFor[IamEntity](request.username).ask(StageUser(request.username)) map { case (token, ts) =>
      logger.debug(s"User '${request.username}' successfully staged on ${tsF.format(ts)} with token '${token}'.")
      api.Result(Info, Some(s"User '${request.username}' successfully staged on ${tsF.format(ts)}."), Some(token.uuid))
      //api.Result(Warn, Some(s"User '${username}' has already been staged at ${tsF.format(ts)}."), Some("E-mail offering to login/recover password has been sent."))
    }
  }

  override def register(token: String): ServiceCall[api.Register, api.Result] = ???

}

