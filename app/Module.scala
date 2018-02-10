import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

import services._
import util.concurrent.{DatabaseExecutionContext, DefaultDatabaseExecutionContext}

class Module extends AbstractModule with ScalaModule {

  def configure() = {
    bind[UserRepository]           . to[DefaultUserRepository]
    bind[DatabaseExecutionContext] . to[DefaultDatabaseExecutionContext]
  }

}
