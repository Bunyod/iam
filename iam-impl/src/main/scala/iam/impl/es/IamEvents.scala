package mdpm
package iam.impl
package es

import java.time.{Duration, Instant}
import java.util.UUID
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

case class UserStaged(
  username : api.EMail,
  timestamp: Instant  = Instant.now()
) extends IamEvent {

  /* NOTE: Not persisted in event store. In case of a replay the tokens are most like to be outdated.
           The read-side, however, persists those mail tokens. */
  val token = MailToken(UUID.randomUUID().toString, timestamp.plus(Duration.ofMinutes(10)))

  override val toString = s"UserStaged(${username}, ${tsF.format(timestamp)}, $token)"

}

object UserStaged {
  implicit val format: Format[UserStaged] = Json.format
}