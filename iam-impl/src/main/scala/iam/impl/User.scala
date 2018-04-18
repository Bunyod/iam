package mdpm
package iam.impl

import play.api.libs.json._
import mdpm.iam.api

/** A user. */
case class User(
  username: api.EMail,
  status  : Status,
  forename: Option[String] = None,
  surname : Option[String] = None,
  password: Option[String] = None,
  role    : Option[Role]   = None
)

//object User {
//  implicit val format: Format[User] = Json.format
//}

// ********************************************************************************************************************
// USER ROLE
// ********************************************************************************************************************

sealed trait Role
case object Admin extends Role
sealed trait Uzer extends Role
case object Owner extends Uzer
sealed trait Contact extends Uzer
case object App extends Contact
case object Chp extends Contact

object Role {
  import julienrf.json.derived
  implicit val format: OFormat[Role] = derived.oformat()
}

object Uzer {
  import julienrf.json.derived
  implicit val format: OFormat[Uzer] = derived.oformat()
}

object Contact {
  import julienrf.json.derived
  implicit val format: OFormat[Contact] = derived.oformat()
}

// ********************************************************************************************************************
// USER STATE
// ********************************************************************************************************************

sealed trait Status
case object Staged extends Status
sealed trait Registered extends Status
case object Active extends Registered
case object Inactive extends Registered

object Status {
  import julienrf.json.derived
  implicit val format: OFormat[Status] = derived.oformat()
}
