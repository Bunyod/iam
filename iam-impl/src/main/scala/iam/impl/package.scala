package mdpm
package iam

import play.api.libs.json.{Format, Json}
import java.time.{Instant, ZoneId}
import java.time.format.{DateTimeFormatter, FormatStyle}
import java.util.Locale

package object impl {

  // Cf. https://stackoverflow.com/a/27483371/540718
  val tsF = DateTimeFormatter
    .ofLocalizedDateTime(FormatStyle.FULL)
    .withLocale(Locale.GERMANY)
    .withZone(ZoneId.systemDefault())


  case class Name(forename: String, surname: String)

  object Name {
    implicit val format: Format[Name] = Json.format
  }

  case class MailToken(uuid: String, expiration: Instant) {
    def isExpired = expiration.isBefore(Instant.now())
  }

  object MailToken {
    implicit val format: Format[MailToken] = Json.format
  }

}
