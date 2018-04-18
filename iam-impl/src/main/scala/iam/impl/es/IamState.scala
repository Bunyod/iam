package mdpm
package iam.impl
package es

import java.time.Instant
import play.api.libs.json._

/** The current state held by the persistent entity. */
case class IamState(
  user     : Option[User],
  timestamp: Instant     = Instant.now()
) {
  override val toString = s"IamState(${user}, ${tsF.format(timestamp)})"
}

//object IamState {
//  implicit val format: Format[IamState] = Json.format
//}

