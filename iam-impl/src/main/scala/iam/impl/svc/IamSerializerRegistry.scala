package mdpm
package iam.impl
package svc

import scala.collection.immutable.Seq
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import mdpm.iam.impl.es.UserStaged

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
    // === Model
    // TODO Required?
    //JsonSerializer[User],
    // === Commands
    // TODO Required?
    //JsonSerializer[StageUser],
    // === Events
    JsonSerializer[UserStaged],
    // === State
    // TODO Required?
    //JsonSerializer[IamState]
  )

}
