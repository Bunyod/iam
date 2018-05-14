package mdpm
package iam.impl
package es

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import mdpm.iam.api
import play.api.libs.json.{Format, Json}

sealed trait IamCommand [R] extends ReplyType[R]

case class StageUser(username: api.EMail) extends IamCommand[UserToken]
case class RegisterUser(forename: String, surname : String, password : String, username: String) extends IamCommand[UserToken]

object StageUser {
  implicit val format: Format[StageUser] = Json.format
}

object RegisterUser {
  implicit val format: Format[RegisterUser] = Json.format
}
