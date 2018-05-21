organization in ThisBuild := "mdpm"

version in ThisBuild := "1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.12.4"

def commonSettings: Seq[Setting[_]] = Seq(
  // API docs:
  autoAPIMappings := true,
  scalacOptions in (Compile, doc) ++= Seq(
    "-doc-title", name.value,
    "-diagrams",
    "-diagrams-dot-path", "/usr/local/bin/dot"
    // "-diagrams-dot-timeout", "20", "-diagrams-debug",
  )
)

lazy val root = (project in file("."))
  .settings(name := "IAM")
  .settings(commonSettings: _*)
  .aggregate(
    `iam-api`, `iam-impl`
  )

//*********************************************************************************************************************
// LIBRARIES
//*********************************************************************************************************************

val jsonExtra     = "org.julienrf"               %% "play-json-derived-codecs" % "4.0.0"
val playMailer    = "com.typesafe.play"          %% "play-mailer"              % "6.0.1"
val macwire       = "com.softwaremill.macwire"   %% "macros"                   % "2.3.0" % "provided"
val scalaLogging  = "com.typesafe.scala-logging" %% "scala-logging"            % "3.9.0"
//val scalaz        = "org.scalaz"                 %% "scalaz-core"              % "7.2.20"
val scalaTest     = "org.scalatest"              %% "scalatest"                % "3.0.4" % Test
val playJWT       = "com.pauldijou"              %% "jwt-play-json"            % "0.16.0"
val scalaJWT      = "com.pauldijou"              %% "jwt-core"                 % "0.16.0"

//*********************************************************************************************************************
// CASSANDRA
//*********************************************************************************************************************

lagomCassandraPort in ThisBuild := 4000

// cf. `./cleanup-kafka.sh`
lagomCassandraCleanOnStart in ThisBuild := false

//lagomCassandraYamlFile in ThisBuild := Some((baseDirectory in ThisBuild).value / "project" / "cassandra.yaml")

//lagomCassandraJvmOptions in ThisBuild := Seq("-Xms256m", "-Xmx1024m", "-Dcassandra.jmx.local.port=4099") // these are actually the default jvm options

//lagomCassandraEnabled in ThisBuild := false
//lagomUnmanagedServices in ThisBuild := Map("cas_native" -> "http://localhost:9042")

//*********************************************************************************************************************
// KAFKA
//*********************************************************************************************************************

lagomKafkaPort in ThisBuild := 9092
lagomKafkaZookeeperPort in ThisBuild := 2181

//lagomKafkaPropertiesFile in ThisBuild := Some((baseDirectory in ThisBuild).value / "project" / "kafka-server.properties")

//lagomKafkaJvmOptions in ThisBuild := Seq("-Xms256m", "-Xmx1024m") // these are actually the default jvm options

//lagomKafkaEnabled in ThisBuild := false
//lagomKafkaAddress in ThisBuild := "localhost:10000"

//*********************************************************************************************************************
// µSes
//*********************************************************************************************************************

// Identity and access management µS {{{1
lazy val `iam-api` = (project in file("iam-api"))
  .settings(commonSettings: _*)
  .settings(
    version              := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      jsonExtra
    )
  )

lazy val `iam-impl` = (project in file("iam-impl"))
  .enablePlugins(LagomScala)
  .dependsOn(`iam-api`)
  .settings(commonSettings: _*)
  .settings(lagomForkedTestSettings: _*)
  .settings(
    version              := "1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      //lagomScaladslKafkaBroker,
      macwire,
      jsonExtra,
      playMailer,
      scalaLogging,
      playJWT,
      scalaJWT,
      lagomScaladslTestKit,
      scalaTest
    ),
    dependencyOverrides ++= Seq(
      "com.github.jnr"     % "jnr-constants"   % "0.9.9",
      //"io.netty"           % "netty"           % "3.10.6.Final",
      "io.netty"           % "netty-handler"   % "4.1.22.Final",
      ////"io.netty"           % "netty-codec"     % "4.1.22.Final",
      ////"io.netty"           % "netty-transport" % "4.1.22.Final",
      ////"io.netty"           % "netty-resolver"  % "4.1.22.Final",
      ////"io.netty"           % "netty-buffer"    % "4.1.22.Final",
      ////"io.netty"           % "netty-common"    % "4.1.22.Final",
      "com.google.guava"   % "guava"            % "22.0",
      "com.typesafe.akka"  %% "akka-stream"     % "2.5.11",
      "com.typesafe.akka"  %% "akka-actor"      % "2.5.11"
    )
  )

// vim: fdm=marker
