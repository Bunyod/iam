package mdpm
package iam.api

import play.api.libs.json.{Format, Json}

//*********************************************************************************************************************
// REQUESTS
//*********************************************************************************************************************

/**
 * The payload for staging an user registration.
 *
 * @param username The user's email address.
 */
case class Signup(username: EMail)

object Signup {
  implicit val format: Format[Signup] = Json.format
}

/**
 * The payload for completing an user registration.
 *
 * @param forename The user's forename.
 * @param surname The user's surname.
 * @param password The password chosen by the user.
 */
case class Register(
  forename: String,
  surname : String,
  password: String
)

object Register {
  implicit val format: Format[Register] = Json.format
}

//*********************************************************************************************************************
// RESPONES
//*********************************************************************************************************************

/**
 * A generic response to API requests.
 *
 * @param `type` The response type.
 * @param subject The response subject.
 * @param details The response details.
 */
case class Result(
  `type` : Result.Type,
  subject: Option[String] = None,
  details: Option[String] = None
)

object Result {
  sealed trait Type
  case object Error extends Type
  case object Warn extends Type
  case object Info extends Type

  import play.api.libs.json._

  // TODO Improve serialization of `api.Result`!
  object Type {
    implicit val writes = new Writes[Type] {
      def writes(t: Type) = t match {
        case Error => JsString("Error")
        case Info => JsString("Info")
        case Warn => JsString("Warn")
      }
    }
    implicit val reads = new Reads[Type] {
      override def reads(json: JsValue): JsResult[Type] = json match {
        case JsString("Error") => JsSuccess(Error)
        case JsString("Info")  => JsSuccess(Info)
        case JsString("Warn")  => JsSuccess(Warn)
        case _                 => JsError("Result.Type expected")
      }
    }
  }

  import julienrf.json.derived
  implicit val format: OFormat[Result] = derived.oformat()
}
