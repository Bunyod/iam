package mdpm
package iam.impl
package es

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.typesafe.scalalogging.StrictLogging
import mdpm.iam.impl.util.JwtTokenUtil

/**
 * This is an event sourced entity with [[IamState]] state.
 *
 * =__Commands__=
 *
 * Event sourced entities are interacted with by sending them commands. This entity supports the following commands
 *
 * ==Write==
 *
 * - ...
 *
 * ==Read==
 *
 * - ...
 *
 * ==Read/Write==
 *
 * - ...
 *
 *
 * =__Events__=
 *
 * This entity defines the following events
 *
 * - ...
 *
 */

class IamEntity() extends PersistentEntity with StrictLogging {

  override type Command = IamCommand[_]
  override type Event = IamEvent
  override type State = IamState

  override val initialState = IamState(None)

  override def behavior: Behavior = stage orElse register //orElse userInfo

  private val stage = Actions()
    .onCommand[StageUser, UserToken] {
    case (su @ StageUser(username), ctx, _) =>
      val token = JwtTokenUtil.generateAuthToken(su)
      val userToken = UserToken(token)
      val event = UserStagedEvt(username, userToken)
      ctx.thenPersist(event) { _ => ctx.reply(userToken) }
    }
    .onEvent {
      case (UserStagedEvt(username, _), _) =>
        IamState(Some(User(username, Staged, password = None)))
    }

  private val register = Actions()
    .onCommand[RegisterUser, UserToken] {
    case (RegisterUser(forename, surname, password, username), ctx, _) =>
      val user = User(username, Active, forename = Some(forename), surname = Some(surname), password = Some(Right(password)))
      val token = JwtTokenUtil.generateUserToken(user)
      val userToken = UserToken(token)
      val event = UserRegisteredEvt(user, userToken)
      ctx.thenPersist(event) { _ => ctx.reply(userToken) }
    }
    .onEvent {
      case (UserRegisteredEvt(user, _), _) =>
        IamState(Some(user))
    }

//  private val userInfo = Actions()
//    .onReadOnlyCommand[GetUserInfo, UserInfo] {
//      case (GetUserInfo, ctx, state) =>
//        state.user match {
//          case None =>
//            ctx.invalidCommand(s"Registered user with ${entityId} can't be found")
//          case Some(user: User) =>
//            ctx.reply(
//              UserInfo(
//                username = user.username,
//                forename = user.forename,
//                surname = user.surname
//              )
//            )
//        }
//    }

}
