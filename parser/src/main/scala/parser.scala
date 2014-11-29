package org.genxml

import GedcomConverters._

import scala.xml._

import org.gedcom4j.parser.GedcomParser

object GenParser {

  /**
   * Parse a file and return a GEDCOM document
   * @param filename
   **/
  def parseFile(filename : String) = {
    val gp = new GedcomParser()
    gp.load(filename)
    gp.gedcom
  }

  /**
   * Read a GEDCOM file and return an XML string
   * @param filename
   **/
  def convertFile(filename : String) = {
    val content = parseFile(filename)
    // 90 chars per line, 4-spaces indent
    val printer = new PrettyPrinter(90, 4)
    printer.format(content.toXML)
  }

  def main(args : Array[String]) {

    if (args.size == 0) {
      println("A GEDCOM file is required.")
      System.exit(-1)
    }

    // TODO write the result in a file as well
    args.foreach(convertFile)
  }

}
