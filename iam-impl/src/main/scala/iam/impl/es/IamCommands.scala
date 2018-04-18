package mdpm
package iam.impl
package es

import java.time.Instant
import play.api.libs.json._
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import mdpm.iam.api

//*********************************************************************************************************************
// COMMANDS
//*********************************************************************************************************************

// TODO I18N support (cf. https://www.playframework.com/documentation/2.6.x/ScalaI18N)
sealed trait IamCommand [R] extends ReplyType[R]

/**
 * Command to stage user for the registration process.
 *
 * Note: `ReplyType` is `Done` as nothing is returned to the client but an e-mail is sent to the user.
 *
 * @param username
 */
case class StageUser(
  username: api.EMail
) extends IamCommand[(MailToken,Instant)]

object StageUser {
  implicit val format: Format[StageUser] = Json.format
}
