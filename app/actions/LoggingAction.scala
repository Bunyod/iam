package actions

import scala.concurrent.Future

import play.api._
import play.api.mvc._

case class Logging[A](action: Action[A]) extends Action[A] {
  def apply(request: Request[A]): Future[Result] = {
    Logger.info("Calling action")
    action(request)
  }

  override def parser = action.parser
  override def executionContext = action.executionContext
}
