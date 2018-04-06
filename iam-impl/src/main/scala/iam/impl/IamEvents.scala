package mdpm
package iam.impl

import java.time.Instant
import play.api.libs.json.{Format, Json}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag}
import mdpm.iam.api

sealed trait IamEvent extends AggregateEvent[IamEvent] {
  def aggregateTag = IamEvent.Tag
}

object IamEvent {
  val Tag = AggregateEventTag[IamEvent]
}

//*********************************************************************************************************************
// User registration
//*********************************************************************************************************************

case class UserStaged(
  username : api.EMail,
  timestamp: Instant  = Instant.now()
) extends IamEvent

object UserStaged {
  implicit val format: Format[UserStaged] = Json.format
}
