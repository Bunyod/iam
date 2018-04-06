package mdpm
package iam.impl

import scala.concurrent.ExecutionContext
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import org.slf4j.LoggerFactory
import mdpm.iam.api

/** Implementation of the IAM µS. */
class IamServiceImpl(registry: PersistentEntityRegistry)(implicit ec: ExecutionContext) extends api.IamService {

  private val logger = LoggerFactory.getLogger(classOf[IamServiceImpl])

  //********************************************************************************************************************
  // User registration
  //********************************************************************************************************************

  override val signup: ServiceCall[api.Signup, api.Result] = ServiceCall { request =>
    registry.refFor[IamEntity](request.username).ask(StageUser(request.username))
  }

  override def register(token: String): ServiceCall[api.Register, api.Result] = ???

}
