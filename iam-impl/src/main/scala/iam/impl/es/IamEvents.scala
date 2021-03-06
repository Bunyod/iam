package mdpm
package iam.impl
package es

import play.api.libs.json.{Format, Json}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag}
import mdpm.iam.api

sealed trait IamEvent extends AggregateEvent[IamEvent] {
  def aggregateTag = IamEvent.Tag
}

object IamEvent {
  val numberOfShards = 2
  val Tag: AggregateEventShards[IamEvent] = AggregateEventTag.sharded[IamEvent](numberOfShards)
}

case class UserStaged(username: api.EMail, token: MailToken) extends IamEvent

case class UserStagedEvt(username: api.EMail, token: UserToken) extends IamEvent
case class UserRegisteredEvt(user: User, token: UserToken) extends IamEvent

object UserStaged {
  implicit val format: Format[UserStaged] = Json.format
}

object UserStagedEvt {
  implicit val format: Format[UserStagedEvt] = Json.format
}

//object UserRegisteredEvt {
//  implicit val format: Format[UserRegisteredEvt] = Json.format
//}
