package models

case class User(
  email    : String
, password : String
, forename : String
, surname  : String
, phone    : String
, role     : String
)

object User {
  import play.api.libs.json._
  import play.api.libs.json.Reads._
  import play.api.libs.functional.syntax._

  implicit val userReads: Reads[User] = (
    (JsPath \ "username").read[String]/*(email)*/ and
    (JsPath \ "password").read[String]/*(minLength[String](6))*/ and
    (JsPath \ "forename").read[String]/*(verifying[String](_.nonEmpty))*/ and
    (JsPath \ "surname").read[String]/*(verifying[String](_.nonEmpty))*/ and
    (JsPath \ "phone").read[String] and
    (JsPath \ "role").readWithDefault[String]("User")
  ) (User.apply _)

  implicit val userWrites: Writes[User] = (u: User) => Json.obj(
      "username" -> u.email
    , "forename" -> u.forename
    , "surename" -> u.surname
    , "phone" -> u.phone
  )
}
