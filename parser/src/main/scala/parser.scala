package edu.parisdiderot.m2.xml.galichetfontaine

import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import scala.io.Source
import scala.collection.JavaConverters._
import scala.xml._

import org.gedcom4j.parser.GedcomParser

object GenParser {

  val idLimitersRegExp = "(^@|@$)".r

  //def individual2xml(id : String, )

  /**
   * Read a GEDCOM stream and return a 
   **/
  def convertStream(stream : BufferedInputStream) : Elem = {
    val gp = new GedcomParser()
    gp.load(stream)

    <document>
      <infos></infos>
      <individuals>
        {
          for ((k, indi) <- gp.gedcom.individuals.asScala) yield {
            List(
              <individual id={ idLimitersRegExp.replaceAllIn(k, "") }>
                TODO
              </individual>,
              "\n")
          }
        }
      </individuals>
      <families>{
        // TODO
      }</families>
    </document>
  }

  /**
   * Read a file, fix non-standard stuff and return a stream
   **/
  def readFile(filename : String) : BufferedInputStream = {
    val source = Source.fromFile(filename)

    // remove {lead,trail}ing spaces as well as empty newlines
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
