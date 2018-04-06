package mdpm
package iam.impl

import scala.collection.immutable.Seq
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import org.slf4j.LoggerFactory
import mdpm.iam.api

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

  override def initialState = IamState(None)

  private val logger = LoggerFactory.getLogger(classOf[IamEntity])

  // @formatter:off
  override def behavior: Behavior = {
    case IamState(None      , ts) => doStage
    case IamState(Some(user), ts) => doWarn orElse doRegister
  }
  // @formatter:on

  // ******************************************************************************************************************
  // Actions
  // ******************************************************************************************************************

  private val doStage = Actions()
    .onCommand[StageUser,api.Result] { stage }
    .onEvent                         { staged }

  private val doWarn = Actions()
    .onCommand[StageUser,api.Result] { warn }

  private val doRegister = Actions.empty

  // ******************************************************************************************************************
  // Command handlers
  // ******************************************************************************************************************

  // === State modifying (write commands)
  // Such a command is a request for the system
  // to perform an action that changes the state of the system

  // Results in [[UserStaged]] event; Cf. [[staged]]
  def stage: PartialFunction[(Command, CommandContext[api.Result], State), Persist] = {
    case (StageUser(username), ctx, state) => ctx.thenPersist(UserStaged(username)) { _ =>
      ctx.reply(api.Result(
        `type`  = api.Result.Info,
        subject = Some(s"User '${username}' was successfully staged on ${tsF.format(state.timestamp)}.")
      ))
    }
  }

  // === Non-state modifying (query commands)

  def warn: PartialFunction[(Command, CommandContext[api.Result], State), Persist] = {
    case (StageUser(username), ctx, IamState(Some(user), ts)) => user.status match {

      case Staged   =>
        ctx.reply(api.Result(
          `type`  = api.Result.Warn,
           subject = Some(s"User '${username}' has already been staged at ${tsF.format(ts)}."),
           details = Some("Note that no new e-mail has been sent for registration.")
        ))
        ctx.done

      case Active   => ???
      case Inactive => ???
    }
  }

  // ******************************************************************************************************************
  // Event handlers
  // ******************************************************************************************************************

  // Note: Event handlers are used both for persisting and replaying events.

  def staged: PartialFunction[(Event, State), State] = {
    case (UserStaged(u, ts), state) =>
      logger.debug(s"User '${u}' successfully staged on ${tsF.format(ts)}.")
      IamState(Some(User(username = u, status = Staged)))
  }

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
