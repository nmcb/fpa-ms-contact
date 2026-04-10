import Dependencies._

ThisBuild / version        := "0.1.0"
ThisBuild / scalaVersion   := ScalaLanguageVersion
ThisBuild / scalacOptions ++= Seq(
  "-encoding", "utf8",
  "-feature",
  "-language:implicitConversions",
  "-language:existentials",
  "-unchecked",
  "-Werror",
  "-deprecation"
)

lazy val server = (project in file("server"))
  .settings(
    name := "server",
    libraryDependencies ++= platformDependencies
  )

lazy val integration = (project in file("it"))
  .dependsOn(server)
  .settings(
    name := "integration-tests",
    publish / skip := true,
    libraryDependencies ++= platformDependencies ++ testDependencies
  )

lazy val contact = (project in file("."))
  .aggregate(server, integration)