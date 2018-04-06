package mdpm
package iam.api

import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

/** The IMA µS API. */
trait IamService extends Service {

  import Service._

  // @formatter:off
  override final def descriptor: Descriptor = named("iam").withCalls(
    restCall(Method.POST, "/signup"              , signup _),
    restCall(Method.POST, "/register/:mailToken" , register _),
    // TODO restCall(Method.GET , "/challenge"   , challenge _),
    // TODO restCall(Method.POST, "/authenticate", authenticate _)
  ).withAutoAcl(true)
  // @formatter:on

  /**
   * Stage a new user for registration.
   *
   * In addition an e-mail is sent to the user asking to complete his registration. The e-mail contains the
   * respective registration link.
   *
   * @example
   * {{{
   * curl -H "Content-Type: application/json"\
   *      -X POST\
   *      -d '{"username": "normen.mueller@gmail.com"}'\
   *      http://localhost:9000/signup
   * }}}
   *
   * @return A [[Result]] of type [[Result.Info]], [[Result.Warn]], or [[Result.Error]].
   */
  def signup: ServiceCall[Signup, Result]

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
   *
   * @return A [[Result]] of type [[Result.Info]], [[Result.Warn]], or [[Result.Error]].
   */
  def register(token: String): ServiceCall[Register, Result]

}
