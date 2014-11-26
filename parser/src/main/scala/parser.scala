package edu.parisdiderot.m2.xml.galichetfontaine

import scala.collection.JavaConverters._
import scala.xml._

import org.gedcom4j.parser.GedcomParser
import org.gedcom4j.model._
import org.gedcom4j.model.IndividualEventType._

object GenParser {

  val idLimitersRegExp = "(^@|@$)".r

  def idToString(k : String) =
    idLimitersRegExp.replaceAllIn(k, "")

  def eventTypeToString(t : IndividualEventType) =
    t.display.toLowerCase

  def makeDocumentXML(g : Gedcom) =
    <document>
      <infos></infos>
      <individuals>
        {
          g.individuals.asScala.map { case (k, indi) =>
            makeIndividualXML(idToString(k), indi)
          }
        }
      </individuals>
      <families>
        {
          g.families.asScala.map { case (k, fam) =>
            makeFamilyXML(idToString(k), fam)
          }
        }
      </families>
    </document>

  def makeIndividualXML(key : String, individual : Individual) =
    <individual id={key}>
      <!-- TODO name, death, etc -->
      <sex>{ individual.sex }</sex>
      { makeEventXML(BIRTH, individual.events) }
      { makeEventXML(DEATH, individual.events) }
      { makeEventXML(BURIAL, individual.events) }
      { makeStringsListXML("email", individual.emails) }
      { makeStringsListXML("faxNumber", individual.faxNumbers) }
      { makeStringsListXML("url", individual.wwwUrls) }
      { makeStringsListXML("phone", individual.phoneNumbers) }
      { makeAddressXML(individual.address) }
      <notes>{
        individual.notes.asScala.map((n) =>
          <note>
            { n.lines.asScala.mkString("\n") }
          </note>
        )
      }</notes>
    </individual>

  def makeFamilyXML(key : String, family : Family) =
    <family id={key}>
      <!-- TODO -->
    </family>

  def makeAddressXML(addr : Address) =
    if (addr != null)
      <address>
        <addr1>{ addr.addr1 }</addr1>
        <addr2>{ addr.addr2 }</addr2>
        <postalCode>{ addr.postalCode }</postalCode>
        <city>{ addr.city }</city>
        <stateOrProvince>{ addr.stateProvince }</stateOrProvince>
        <country>{ addr.country }</country>
      </address>

  def makeEventXML(type_ : IndividualEventType,
                   events : java.util.List[IndividualEvent]) =
    events.asScala.find(_.`type` == type_) match {
      case Some(ev) =>
        <xml>
          <date>{ev.date}</date>
          { if (type_ == DEATH && ev.cause != null) <cause>{ev.cause}</cause> }
          { if (ev.place != null) <place>{ev.place.placeName}</place> }
          <!-- TODO -->
        </xml>.copy(label=eventTypeToString(type_))
      case None => ""
    }

  /**
   * Return an XML '{baseTag}s' element that contains one '{baseTag}' element
   * per {lst}'s element. For example:
   *    makeStringListXML("foo", ("bar", "qux")) =
   *      <foos>
   *        <foo>bar</foo>
   *        <foo>qux</foo>
   *      </foos>
   **/
  def makeStringsListXML(baseTag : String,
                         lst : java.util.List[StringWithCustomTags]) =
    <xml>{
      lst.asScala.map((s) => <xml>{s}</xml>.copy(label=baseTag))
    }</xml>.copy(label = baseTag + "s")

  def parseFile(filename : String) = {
    val gp = new GedcomParser()
    gp.load(filename)
    gp.gedcom
  }

  /**
   * Read a GEDCOM file and return an XML string
   **/
  def convertFile(filename : String) = {
    val xml = makeDocumentXML(parseFile(filename))
    // 90 chars per line, 4-spaces indent
    val printer = new PrettyPrinter(90, 4)
    printer.format(xml)
  }

  def main(args : Array[String]) {

    if (args.size == 0) {
      println("A GEDCOM file is required.")
      System.exit(-1)
    }

    args.foreach(convertFile)
  }

}
