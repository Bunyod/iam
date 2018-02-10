package util.concurrent

import scala.concurrent.ExecutionContextExecutor

import play.api.libs.concurrent.CustomExecutionContext

import akka.actor.ActorSystem
import javax.inject._

trait DatabaseExecutionContext
  extends ExecutionContextExecutor

@Singleton
class DefaultDatabaseExecutionContext @Inject()(system: ActorSystem)
  extends CustomExecutionContext(system, "ec.db.dispatcher")
  with    DatabaseExecutionContext
