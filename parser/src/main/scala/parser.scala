package org.genxml

import java.io.{BufferedWriter, FileWriter}

import scala.collection.JavaConverters._
import scala.language.implicitConversions
import scala.xml.PrettyPrinter

import org.gedcom4j.parser.GedcomParser
import org.gedcom4j.model.Gedcom

import GedcomConverters._

object GenParser {

  /**
   * Parse a file and return a GEDCOM document
   * @param filename
   **/
  def parseFile(filename : String, verbose : Boolean) = {
    val gp = new GedcomParser()
    gp.load(filename)
    if (verbose) {
      gp.errors.asScala.toList.foreach(e => println("Error: " + e))
      gp.warnings.asScala.toList.foreach(w => println("Warning: " + w))
    }
    gp.gedcom
  }

  def parseFile(filename : String) : Gedcom = parseFile(filename, false)

  /**
   * Read a GEDCOM file and return an XML string
   * @param filename
   **/
  def convertFile(filename : String) = {
    val content = parseFile(filename)
    val printer = new PrettyPrinter(90, 4)
    printer.format(content.toXML)
  }

  def convertFile(inputFilename : String, outputFilename : String) {
    val xml = convertFile(inputFilename)
    val writer = new BufferedWriter(new FileWriter(outputFilename))
    writer.write(xml)
    writer.close()
  }

  def main(args : Array[String]) {

    if (args.size <= 1) {
      println("Usage:\n<program> <gedcom source> <xml target>")
      System.exit(-1)
    }

    convertFile(args(0), args(1))
  }

}
