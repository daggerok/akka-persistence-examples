val akkaVersion = "2.6.1"

lazy val `akka-persistence-postgres-starter` = project
  .in(file("."))
  .settings(
    organization := "com.github.daggerok.akka",
    version := "1.0.0",
    scalaVersion := "2.13.1",
    scalacOptions in Compile ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint"),
    javacOptions in Compile ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),
    libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
        "com.typesafe.akka" %% "akka-persistence-typed" % akkaVersion,
        "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
        "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion,
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
        "ch.qos.logback" % "logback-classic" % "1.2.3",
        // jdbc-pg
        "com.github.dnvriend" %% "akka-persistence-jdbc" % "3.5.2",
        "org.postgresql" % "postgresql" % "42.2.5",
        // test
        "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
        "org.scalatest" %% "scalatest" % "3.0.8" % Test,
    ),
    fork in run := false,
    Global / cancelable := false, // ctrl-c
    mainClass in (Compile, run) := Some("com.github.daggerok.akka.Main"),
    // disable parallel tests
    parallelExecution in Test := false,
    // show full stack traces and test case durations
    testOptions in Test += Tests.Argument("-oDF"),
    logBuffered in Test := false,
    licenses := Seq(("MIT", url("https://github.com/daggerok/akka-persistence-examples/blob/master/LICENSE"))),
    composeFile := "docker-compose.yaml"
  )

enablePlugins(DockerComposePlugin)
