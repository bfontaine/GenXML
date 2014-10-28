package edu.parisdiderot.m2.xml.galichetfontaine

import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import scala.io.Source
import scala.collection.JavaConverters._

import org.gedcom4j.parser.GedcomParser

object GenParser {

  val nonEmptyLineRegExp = "\\S".r.pattern.matcher _

  /**
   * Read a GEDCOM stream and return a 
   **/
  def convertStream(stream : BufferedInputStream) {
    val gp = new GedcomParser()
    gp.load(stream)

    // tests
    gp.gedcom.individuals.asScala.foreach {case (k, _) =>
      println(<individual>{k}</individual>)
    }

    // TODO
  }

  /**
   * Read a file, fix non-standard stuff and return a stream
   **/
  def readFile(filename : String) : BufferedInputStream = {
    val source = Source.fromFile(filename)
    val content = "\\s*\n\\s+".r.replaceAllIn(source.mkString, "\n")

    new BufferedInputStream(new ByteArrayInputStream(content.getBytes()))
  }

  def convertFile(filename : String) =
    convertStream(readFile(filename))

  def main(args : Array[String]) {

    if (args.size == 0) {
      println("A GEDCOM file is required.")
      System.exit(-1)
    }

    args.foreach(convertFile)
  }

}
