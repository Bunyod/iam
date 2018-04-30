package mdpm
package iam.impl
package es

import java.time.{Duration, Instant}
import java.util.UUID
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.typesafe.scalalogging.StrictLogging

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

  override def behavior: Behavior = stage orElse register

  private val stage = Actions()
    .onCommand[StageUser, MailToken] {
      case (StageUser(username), ctx, _) =>
        val token = MailToken(UUID.randomUUID().toString, Instant.now().plus(MailToken.DURATION))
        val event = UserStaged(username, token)
        ctx.thenPersist(event) { e => ctx.reply(e.token) }
    }
    .onEvent {
      case (UserStaged(username, token), _) =>
        IamState(Some(User(username, Staged, password = Some(Left(token)))))
    }

  private val register = Actions.empty

}
