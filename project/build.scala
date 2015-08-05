import sbt._
import Keys._

import spray.revolver.RevolverPlugin._

object TopLevelBuild extends Build {

  val appName = "communicator"

  lazy val root = Project (
    id = appName,
    base = file ("."),
    settings = Settings.buildSettings ++
      Seq (
        resolvers ++= Resolvers.allResolvers,
        libraryDependencies ++= Dependencies.allDependencies
      ) ++
      Revolver.settings ++
      net.virtualvoid.sbt.graph.Plugin.graphSettings
  )
}

object Settings {
  val buildOrganization = "http://justinrmiller.com"
  val buildVersion      = "1.0.0"
  val buildScalaVersion = "2.11.7"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization  := buildOrganization,
    version       := buildVersion,
    scalaVersion  := buildScalaVersion,
    scalacOptions := Seq("-deprecation", "-feature", "-encoding", "utf8")
  )
}
