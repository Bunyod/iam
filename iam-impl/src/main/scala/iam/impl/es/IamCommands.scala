package mdpm
package iam.impl
package es

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import mdpm.iam.api

sealed trait IamCommand [R] extends ReplyType[R]

case class StageUser(username: api.EMail) extends IamCommand[MailToken]

//object StageUser {
//  implicit val format: Format[StageUser] = Json.format
//}
