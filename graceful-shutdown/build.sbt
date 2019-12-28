lazy val akkaVersion = "2.6.1"

lazy val `graceful-shutdown` = project
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
      "com.h2database" % "h2" % "1.4.200",
      // test
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
      "org.scalatest" %% "scalatest" % "3.0.8" % Test,
    ),
    fork in run := false,
    Global / cancelable := false, // ctrl-c
    mainClass in(Compile, run) := Some("com.github.daggerok.akka.Main"),
    // disable parallel tests
    parallelExecution in Test := false,
    // show full stack traces and test case durations
    testOptions in Test += Tests.Argument("-oDF"),
    logBuffered in Test := false,
    licenses := Seq(("MIT", url("https://github.com/daggerok/akka-persistence-examples/blob/master/LICENSE"))),
    // h2 tasks
    h2Mem := h2("mem"),
    h2FileInit := h2("file-init"),
    h2FileNext := h2("file-next"),
  )

lazy val h2Mem = TaskKey[Unit]("h2Mem")
lazy val h2FileInit = TaskKey[Unit]("h2FileInit")
lazy val h2FileNext = TaskKey[Unit]("h2FileNext")

lazy val h2: String => Unit =
  profile => {
    import java.nio.file._
    val source = new File(s"src/main/resources/application-${profile}.conf")
    val target = new File("src/main/resources/application.conf")
    Files.copy(source.toPath, target.toPath, StandardCopyOption.REPLACE_EXISTING)
  }
