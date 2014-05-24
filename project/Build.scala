import sbt._
import Keys._
import play.Play.autoImport._
import PlayKeys._

object ApplicationBuild extends Build {

  val appName         = "securesocial-poc"
  val appVersion      = "1.1-SNAPSHOT"

  scalaVersion := "2.10.4"

  val appDependencies = Seq(
    // Need JDBC for the database evolution scripts.
    jdbc,

    // Need WS for LinkedInOauth2Provider
    ws,

    // Slick
    "com.typesafe.slick" %% "slick" % "2.0.0",
    "com.h2database" % "h2" % "1.3.166",

    // Joda Time mapper for slick
    "joda-time" % "joda-time" % "2.3",
    "org.joda" % "joda-convert" % "1.5",
    "com.github.tototoshi" %% "slick-joda-mapper" % "1.0.0",

    // SecureSocial
    "ws.securesocial" %% "securesocial" % "2.1.3",

    // Mailer plugin
    "com.typesafe" %% "play-plugins-mailer" % "2.2",

    // include bootstrap CSS template
    "org.webjars" %% "webjars-play" % "2.2.1",
    "org.webjars" % "bootstrap" % "3.1.0",

    "org.fluentlenium" % "fluentlenium-festassert" % "0.9.2" % "test"
  )

  val main = Project(appName, file(".")).enablePlugins(play.PlayScala).settings(
    version := appVersion,
    libraryDependencies ++= appDependencies,
    resolvers += Resolver.sonatypeRepo("releases")
  )

}
