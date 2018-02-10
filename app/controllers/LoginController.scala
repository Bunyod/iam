package controllers

import scala.concurrent.Future

import play.api.http.{ContentTypeOf, ContentTypes, Writeable}
import play.api.libs.json.{JsError, Json, Reads}
import play.api.mvc._
import play.api.{Configuration, Logger}

import javax.inject._

import models.User
import services.UserRepository

@Singleton
class LoginController @Inject()(cc: ControllerComponents)(implicit
    config        : Configuration
  , userRepository: UserRepository
) extends AbstractController(cc) {

  import models.User._

  private implicit val ec = cc.executionContext

  def login() = Action.async { implicit request: Request[AnyContent] => Future {
    Ok(views.html.login(config.get[String]("version"))).withNewSession
  }(ec) }

  private def validateJson[A: Reads]: BodyParser[A] = parse.json.validate(
    _.validate[A].asEither.left.map(e => {
      Logger.error(Json.prettyPrint(JsError.toJson(e)))
      BadRequest(JsError.toJson(e))
    })
  )(ec)

  implicit def wLong(implicit codec: Codec): Writeable[Long] =
    Writeable((l: Long) => codec.encode(l.toString))(ContentTypeOf[Long](Some(ContentTypes.TEXT)))

  //def register() = Action.async(parse.json[User]) { implicit request: Request[User] =>
  def register() = Action.async(validateJson[User]) { implicit request: Request[User] =>
    Logger.debug(request.headers.toString)
    Logger.debug(request.body.toString())
    Logger.debug((1 to 80 map (_ => "=")).mkString)

    userRepository.add(request.body) map (_ fold (
      x => {
        Logger.debug(Json.prettyPrint(Json.parse(s"""{ "error" : "${x}"}""")))
        Ok(Json.parse(s"""{ "error" : "${x}"}"""))
      }
    , x => Ok(Json.parse(s"""{ }"""))
    ))
  }

}
