package edu.parisdiderot.m2.xml.galichetfontaine

import scala.collection.JavaConverters._

import org.gedcom4j.parser.GedcomParser
import org.apache.commons.io.FilenameUtils

object GenParser {

  /**
   * Read a Gedcom file whose path is given as an argument, and produce an XML
   * file with this file's basename and the XML extension in the current
   * directory.
   **/
  def convertFile(filename : String) {
    val destname = FilenameUtils.getBaseName(filename) + ".xml"
    val gp = new GedcomParser()
    gp.load(filename)

    // tests
    gp.gedcom.individuals.asScala.foreach {case (k, _) =>
      println(<individual>{k}</individual>)
    }

    // TODO
  }

  def main(args : Array[String]) {

    if (args.size == 0) {
      println("A GEDCOM file is required.")
      System.exit(-1)
    }

    args.foreach(convertFile)
  }

}
