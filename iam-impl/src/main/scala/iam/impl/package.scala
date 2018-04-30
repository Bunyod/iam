package mdpm
package iam

import java.time.{Instant, ZoneId}
import java.time.format.{DateTimeFormatter, FormatStyle}
import java.util.Locale
import play.api.libs.json._

package object impl {

  type Password = String

  // Cf. https://stackoverflow.com/a/27483371/540718
  val tsF = DateTimeFormatter
    .ofLocalizedDateTime(FormatStyle.FULL)
    .withLocale(Locale.GERMANY)
    .withZone(ZoneId.systemDefault()) //.withZone(ZoneId.of("Europe/Berlin"))

  implicit val defaultInstantReads = Reads.instantReads(tsF)

  implicit val defaultInstantWrites = new Writes[Instant] {
    def writes(i: Instant): JsValue = JsString(tsF.format(i))
  }

  case class Name(forename: String, surname: String)

  object Name {
    implicit val format: Format[Name] = Json.format
  }

}
