name := """IAM"""

organization := "de.safeplace"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(libraryDependencies -= "com.typesafe.play" %% "play-test" % play.core.PlayVersion.current % "test")

scalaVersion := "2.12.4"

// Setup
// ~~~
/* `guice` resolves to 'com.typesafe.play:play-guice:play.core.PlayVersion.current' (cf. `play.sbt.PlayImport`) */
libraryDependencies += guice
/* Imported by `guice` or `play-test`. In order to totally remove `guice`, remove `play-guice` and `play-test`.
   The latter is auto-imported by `PlayScala` (cf. `play.sbt.PlaySettings`) */
//libraryDependencies += "com.google.inject" % "guice" % "4.1.0"
libraryDependencies += "net.codingwell" %% "scala-guice" % "4.1.1"

// Security
// ~~~
libraryDependencies += "com.mohiva" %% "play-silhouette"                 % "5.0.0"
libraryDependencies += "com.mohiva" %% "play-silhouette-password-bcrypt" % "5.0.0"
libraryDependencies += "com.mohiva" %% "play-silhouette-crypto-jca"      % "5.0.0"
libraryDependencies += "com.mohiva" %% "play-silhouette-persistence"     % "5.0.0"

// Database
// ~~~
libraryDependencies += jdbc
libraryDependencies += evolutions
libraryDependencies += "org.playframework.anorm" %% "anorm"                % "2.6.0"
libraryDependencies += "mysql"                    % "mysql-connector-java" % "6.0.6"

// Web
// ~~~
libraryDependencies += "org.webjars" % "jquery" % "3.3.1"

// Testing
// ~~~
//libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

// avoid warnings about dependency version conflicts
dependencyOverrides ++= Seq(
  "com.google.guava"         % "guava" % "22.0"
, "com.typesafe.akka"        % "akka-stream_2.12" % "2.5.8"
, "com.typesafe.akka"        % "akka-actor_2.12" % "2.5.8"
, "com.google.code.findbugs" % "jsr305" % "2.0.1"
)
