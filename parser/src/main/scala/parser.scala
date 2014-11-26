package edu.parisdiderot.m2.xml.galichetfontaine

import scala.collection.JavaConverters._
import scala.xml._

import org.gedcom4j.parser.GedcomParser

object GenParser {

  val idLimitersRegExp = "(^@|@$)".r

  //def individual2xml(id : String, )

  /**
   * Read a GEDCOM stream and return a 
   **/
  def convertFile(filename : String) : Elem = {
    val gp = new GedcomParser()
    gp.load(filename)

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

  def main(args : Array[String]) {

    if (args.size == 0) {
      println("A GEDCOM file is required.")
      System.exit(-1)
    }

    args.foreach(convertFile)
  }

}
