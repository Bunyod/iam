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

object UserStaged {
  implicit val format: Format[UserStaged] = Json.format
}
