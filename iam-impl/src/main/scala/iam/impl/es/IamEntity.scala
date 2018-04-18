package mdpm
package iam.impl
package es

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import org.slf4j.LoggerFactory
import java.time.Instant

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
class IamEntity() extends PersistentEntity {

  override type Command = IamCommand[_]
  override type Event = IamEvent
  override type State = IamState

  override val initialState = IamState(None)

  private val logger = LoggerFactory.getLogger(classOf[IamEntity])

  // @formatter:off
  override def behavior: Behavior = stage orElse register
//  override def behavior: Behavior = {
//    case IamState(None      , ts) => stage
//    case IamState(Some(user), ts) => stage orElse register //warn orElse register
//  }
  // @formatter:on

  // ******************************************************************************************************************
  // Actions
  // ******************************************************************************************************************

  private val stage = Actions()
    .onCommand[StageUser,(MailToken,Instant)] {
      case (StageUser(username), ctx, _) =>
        ctx.thenPersist(UserStaged(username)) { event => ctx.reply((event.token,event.timestamp)) }
    }
    .onEvent { case (UserStaged(username, ts), _) =>
      IamState(Some(User(username, Staged)))
    }

  private val register = Actions.empty

}

