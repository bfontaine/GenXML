import com.github.retronym.SbtOneJar._

oneJarSettings

name := "gedcom2xml"

version := "1.0"

scalaVersion := "2.10.3"

scalacOptions += "-feature"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

libraryDependencies += "org.gedcom4j" % "gedcom4j" % "2.1.9"
