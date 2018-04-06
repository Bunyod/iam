// Settings for sbt itself!

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

// avoid warnings about dependency version conflicts
dependencyOverrides ++= Seq(
  "org.webjars"         % "webjars-locator-core" % "0.33",
  "org.codehaus.plexus" % "plexus-utils"         % "3.0.17",
  "com.github.jnr"      % "jnr-constants"        % "0.9.0",
  "com.google.guava"    % "guava"                % "22.0",
  "io.netty"            % "netty-handler"        % "4.1.22.Final",
  "com.typesafe.akka"  %% "akka-stream"          % "2.5.11",
  "com.typesafe.akka"  %% "akka-actor"           % "2.5.11"
)
