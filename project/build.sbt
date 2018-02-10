// Settings for sbt itself

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

// avoid warnings about dependency version conflicts
dependencyOverrides ++= Seq(
  "org.webjars" % "webjars-locator-core" % "0.33"
, "org.codehaus.plexus" % "plexus-utils" % "3.0.17"
, "com.google.guava" % "guava" % "23.0"
, "com.typesafe.akka" % "akka-stream_2.12" % "2.5.8"
, "com.typesafe.akka" % "akka-actor_2.12" % "2.5.8")
