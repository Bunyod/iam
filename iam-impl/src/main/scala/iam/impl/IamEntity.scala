package mdpm
package iam.impl

import java.util.UUID
import scala.collection.immutable.Seq
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import org.slf4j.LoggerFactory
import mdpm.iam.api
import java.time.{Duration, Instant}

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
    .onCommand[StageUser,MailToken] {
      case (StageUser(username), ctx, _) =>
        ctx.thenPersist(UserStaged(username)) { event => ctx.reply(event.token) }
    }
    .onEvent { case (UserStaged(username, ts), _) =>
      IamState(Some(User(username, Staged)))
    }

  private val register = Actions.empty

}

/* Akka serialization, used by both persistence and remoting, needs to have
 * serializers registered for every type serialized or deserialized. While it's
 * possible to use any serializer you want for Akka messages, out of the box
 * Lagom provides support for JSON, via this registry abstraction.
 *
 * The serializers are registered here, and then provided to Lagom in the
 * application loader.
 */
object IamSerializerRegistry extends JsonSerializerRegistry {

  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[IamState],
    JsonSerializer[UserStaged]
  )

}
