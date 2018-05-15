package mdpm
package iam.impl

import play.api.libs.json._
import mdpm.iam.api

/** A user. */
case class User(
  username: api.EMail,
  status  : Status,
  forename: Option[String]                     = None,
  surname : Option[String]                     = None,
  password: Option[Either[MailToken,Password]] = None,
  role    : Option[Role]                       = None
) {
  def toUserInfo: UserInfo = {
    UserInfo(
      username = username,
      forename = forename,
      surname  = surname
    )
  }
}

case class UserInfo(
                 username: api.EMail,
                 forename: Option[String]                     = None,
                 surname : Option[String]                     = None
               )

object UserInfo {
  implicit val format: Format[UserInfo] = Json.format
}

// ********************************************************************************************************************
// USER ROLE
// ********************************************************************************************************************

sealed trait Role {
  val value: Int
}
case object Admin extends Role {
  val value = 0
}
sealed trait Uzer extends Role
case object Owner extends Uzer {
  val value = 1
}
sealed trait Contact extends Uzer
case object App extends Contact {
  val value = 2
}
case object Chp extends Contact {
  val value = 3
}

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

sealed trait Status {
  val value: Int
}
case object Staged extends Status {
  val value = 0
}
sealed trait Registered extends Status
case object Active extends Registered {
  val value = 1
}
case object Inactive extends Registered {
  val value = -1
}

object Status {
  import julienrf.json.derived
  implicit val format: OFormat[Status] = derived.oformat()
}
