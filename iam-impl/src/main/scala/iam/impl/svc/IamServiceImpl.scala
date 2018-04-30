package mdpm
package iam.impl
package svc

import scala.concurrent.ExecutionContext
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import org.slf4j.LoggerFactory
import play.api.libs.mailer._
import com.typesafe.scalalogging.StrictLogging
import mdpm.iam.api
import mdpm.iam.api.Result.Info
import mdpm.iam.impl.es.{IamEntity, StageUser}

/** Implementation of the IAM µS. */
class IamServiceImpl(
  registry: PersistentEntityRegistry,
  mailer  : MailerClient
)(implicit ec: ExecutionContext) extends api.IamService with StrictLogging {

  override val signup: ServiceCall[api.Signup, api.Result] = ServiceCall { request =>
    registry.refFor[IamEntity](request.username).ask(StageUser(request.username)) map { token =>
      logger.debug(s"User '${request.username}' successfully staged on ${tsF.format(token.expiration.minus(MailToken.DURATION))} with token '${token.uuid}'.")
      api.Result(Info, Some(s"User '${request.username}' successfully staged on ${tsF.format(token.expiration.minus(MailToken.DURATION))}."), Some(token.uuid))
    }
  }

  override def register(token: String): ServiceCall[api.Register, api.Result] = ???

}

