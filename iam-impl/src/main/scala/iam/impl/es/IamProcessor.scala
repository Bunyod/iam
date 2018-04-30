package mdpm
package iam.impl
package es

import scala.concurrent.ExecutionContext
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, ReadSideProcessor}

/* Transform the events generated by the Persistent Entities into database tables.
 * It will consume events produced by persistent entities and update the database table. */
class IamProcessor(session: CassandraSession, readSide: CassandraReadSide)(implicit ec: ExecutionContext)
  extends ReadSideProcessor[IamEvent] {

  private val iamRepository = new IamRepository(session)

  /* In order to consume events from a read-side, the events need to be tagged. */
  override def aggregateTags: Set[AggregateEventTag[IamEvent]] =
    IamEvent.Tag.allTags

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[IamEvent] =
    readSide.builder[IamEvent]("iamEventOffset")
      .setGlobalPrepare(iamRepository.createTable _)
      .setPrepare(_ => iamRepository.createPreparedStatements)
      .setEventHandler[UserStaged](e => iamRepository.stageUser(e.event))
      .build()

}
