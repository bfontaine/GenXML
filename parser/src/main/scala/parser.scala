package org.genxml

import java.io._

import scala.language.implicitConversions
import scala.collection.JavaConverters._
import scala.xml.PrettyPrinter
import scala.io.Source

import org.gedcom4j.parser.GedcomParser
import org.gedcom4j.model.Gedcom

import GedcomConverters._

object GenParser {

  /**
   * Read a file and return a {BufferedInputStream} representing the file
   * content without leading/trailing spaces nor empty lines
   * @param filename path
   **/
  def readFile(filename : String, encoding : String) = {
    val source = Source.fromFile(filename, encoding)
    // remove {lead,trail}ing spaces as well as empty lines
    val content = "\\s*\n\\s+".r.replaceAllIn(source.mkString, "\n")

    new BufferedInputStream(new ByteArrayInputStream(content.getBytes()))
  }

  /**
   * Try to parse a file using different encodings. It'll return None if no
   * encoding works, and an {Option[GedcomParser]} if it was able to parse the
   * file.
   **/
  def tryToParseFile(filename : String, encodings : List[String]) : Option[GedcomParser] =
    encodings match {
      case Nil => None
      case encoding :: encodings =>
        val gp = new GedcomParser()
        try {
          gp.load(readFile(filename, encoding))
          Some(gp)
        } catch {
          case e : Exception =>
            // try again with the next encoding
            tryToParseFile(filename, encodings)
        }
    }

  def tryToParseFile(filename : String) : Option[GedcomParser] =
    tryToParseFile(filename, List("utf-8", "latin1"))

  /**
   * Parse a file and return a GEDCOM document
   * @param filename
   * @param verbose if true, parsing warnings & errors will be printed
   **/
  def parseFile(filename : String, verbose : Boolean) = {
    tryToParseFile(filename) match {
      case None =>
        println("Error: cannot parse the file.")
        System.exit(-1)
        null // this is for the typer
      case Some(gp) =>
        if (verbose) {
          gp.errors.asScala.toList.foreach(e => println("Error: " + e))
          gp.warnings.asScala.toList.foreach(w => println("Warning: " + w))
        }
        gp.gedcom
    }
  }

  /**
   * Parse a file and return a GEDCOM document
   * @param filename path
   **/
  def parseFile(filename : String) : Gedcom = parseFile(filename, false)

  /**
   * Read a GEDCOM file and return an XML string
   * @param filename path
   **/
  def convertFile(filename : String) = {
    val content = parseFile(filename)
    val printer = new PrettyPrinter(110, 4)
    printer.format(content.toXML)
  }

  /**
   * Read a GEDCOM file and write its XML representation in another file,
   * creating it if it doesn’t exist
   * @param inputFilename GEDCOM input file path
   * @param outputFilename XML output file path
   **/
  def convertFile(inputFilename : String, outputFilename : String) {
    val xml = convertFile(inputFilename)
    val writer = new BufferedWriter(new FileWriter(outputFilename))
    writer.write(xml)
    writer.close()
  }

  def printConvertedFile(filename : String) = println(convertFile(filename))

  def main(args : Array[String]) {
    args.toList match {
      // <program> abc.ged
      case List(input) => printConvertedFile(input)
      // <program> abc.ged -
      case List(input, "-") => printConvertedFile(input)
      // <program> abc.ged abc.xml
      case List(input, output) => convertFile(input, output)
      case _ =>
        println("Usage:\n<program> <gedcom source> [<xml target>]")
        System.exit(-1)
    }
  }

}
