package mdpm
package iam.impl
package svc

import play.api.libs.mailer.MailerComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import mdpm.iam.api.IamService
import mdpm.iam.impl.es.{IamEntity, IamProcessor, IamRepository}

class IamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new IamApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new IamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[IamService])

}

// Note: [[MailerComponents#config]] is fulfilled by [[LagomApplication]] and
// [[LagomConfigComponent]], in partiular.
abstract class IamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
     with CassandraPersistenceComponents
     with AhcWSComponents
     with MailerComponents {

  // Bind the service that this server provides
  // LagomApplication#LagomServerComponents#lagomServer
  override lazy val lagomServer = serverFor[IamService](wire[IamServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = IamSerializerRegistry

  // Register the IAM µS persistent entity
  persistentEntityRegistry.register(wire[IamEntity])

  lazy val repository: IamRepository = wire[IamRepository]

  // Register the read side processor persistent entity
  readSide.register(wire[IamProcessor])

}
