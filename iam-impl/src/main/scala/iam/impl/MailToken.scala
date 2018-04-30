package mdpm
package iam.impl

import java.time.{Duration, Instant}
import play.api.libs.json._

case class MailToken(
  uuid      : String,
  expiration: Instant
) {

  def isExpired: Boolean =
    expiration.isBefore(Instant.now())

  override val toString =
    s"MailToken(uuid = $uuid, expiration = ${tsF.format(expiration)})"

}

object MailToken {
  implicit val format: Format[MailToken] = Json.format

  val DURATION = Duration.ofMinutes(10)
}
