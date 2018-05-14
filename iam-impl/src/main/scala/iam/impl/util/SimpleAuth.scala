package mdpm
package iam.impl
package util

import com.lightbend.lagom.scaladsl.api.transport.{Forbidden, RequestHeader}
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import mdpm.iam.impl.util.JwtTokenUtil._
import mdpm.iam.impl.util.ValidationUtil._

object SimpleAuth {

  def authenticated[Request, Response](serviceCall: (UserInfo, String) => ServerServiceCall[Request, Response]) =
    ServerServiceCall.compose { requestHeader =>
      val tokenContent = extractTokenContent(requestHeader)
      val authToken = extractTokenHeader(requestHeader)

      tokenContent match {
        case Some(token) => serviceCall(token, authToken.getOrElse(""))
        case _ => throw Forbidden("Authorization token is invalid")
      }
    }

  private def extractTokenHeader(requestHeader: RequestHeader) = {
    requestHeader.getHeader("Authorization")
      .map(header => sanitizeToken(header))
  }

  private def extractTokenContent[Response, Request](requestHeader: RequestHeader) = {
    extractTokenHeader(requestHeader)
      .filter(rawToken => isValidToken(rawToken))
      .map(rawToken => decodeToken(rawToken))
  }

  private def sanitizeToken(header: String) = header.replaceFirst("Bearer ", "")



}
