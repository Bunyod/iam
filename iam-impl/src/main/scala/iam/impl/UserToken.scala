package mdpm
package iam.impl

import java.sql.Timestamp
import java.util.Date

import mdpm.iam.impl.util.DateUtcUtil
import play.api.libs.json._
import mdpm.iam.impl.util.JwtTokenUtil._
import pdi.jwt.JwtJson
case class UserToken(
  token: String
) {

  def isExpired: Boolean =
    JwtJson.decode(token, secret, Seq(algorithm)).get.expiration.get < DateUtcUtil.now().getMillis

  def expiration: Date =
    new Timestamp(
          JwtJson.decode(token, secret, Seq(algorithm)).get.expiration.get
    )

  def issuedAt: Date =
    new Timestamp(
      JwtJson.decode(token, secret, Seq(algorithm)).get.issuedAt.get
    )

}

object UserToken {
  implicit val format: Format[UserToken] = Json.format
}
