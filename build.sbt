name := "webserverSample"

version := "1.0"

scalaVersion := "2.11.7"

//mainClass in (Compile, run) := Some("in.Sample.Server.Runnr")

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4-SNAPSHOT"

    