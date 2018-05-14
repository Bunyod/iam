package mdpm
package iam.impl
package util

import mdpm.iam.impl.util.JwtTokenUtil._
import pdi.jwt.Jwt

object ValidationUtil {

  private val emailRegex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r

  def isValidEmail(email: String): Boolean = {
    if (email == null) {
      false
    } else {
      emailRegex.findFirstIn(email).nonEmpty
    }
  }

  def isValidToken(token: String): Boolean = Jwt.isValid(token, secret, Seq(algorithm))

}


