name := "olx-notificator"

version := "1.0"

scalaVersion := "2.12.1"

resolvers += "lightshed-maven" at "http://dl.bintray.com/content/lightshed/maven"

libraryDependencies ++= Seq(
  "net.ruippeixotog" %% "scala-scraper" % "1.2.0",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "ch.lightshed" %% "courier" % "0.1.4"
)

lazy val root = (project in file(".")).enablePlugins(SbtTwirl)

TwirlKeys.templateImports += "model.Offer"