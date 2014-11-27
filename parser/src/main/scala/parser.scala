package org.genxml

import scala.collection.JavaConverters._
import scala.xml._

import org.gedcom4j.parser.GedcomParser
import org.gedcom4j.model._
import org.gedcom4j.model.IndividualEventType._

object GenParser {

  val idLimitersRegExp = "(^@|@$)".r

  /**
   * Convert a GEDCOM id into a string. It currently only removes leading and
   * trailing @'s.
   * @param k the id
   **/
  def idToString(k : String) =
    idLimitersRegExp.replaceAllIn(k, "")

  /**
   * Return a string describing an individual event type, usable as an XML tag
   * @param t the event type
   **/
  def eventTypeToString(t : IndividualEventType) =
    t.display.toLowerCase

  /**
   * Convert a GEDCOM document into an XML element
   * @param g the GEDCOM document, usually obtained by parsing a file
   **/
  def makeDocumentXML(g : Gedcom) =
    <document>
      <infos></infos>
      <individuals>
        {
          g.individuals.asScala.map { case (_, indi) =>
            makeIndividualXML(indi)
          }
        }
      </individuals>
      <families>
        {
          g.families.asScala.map { case (_, fam) =>
            makeFamilyXML(fam)
          }
        }
      </families>
    </document>

  /**
   * Convert an individual document into an XML element
   * @param individual the GEDCOM individual
   **/
  def makeIndividualXML(individual : Individual) =
    <individual id={idToString(individual.xref)}>
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
        individual.notes.asScala.map(n =>
          <note>
            { n.lines.asScala.mkString("\n") }
          </note>
        )
      }</notes>
    </individual>

  /**
   * Convert a family document into an XML element
   * @param family the GEDCOM family
   **/
  def makeFamilyXML(family : Family) =
    <family id={idToString(family.xref)}>
      <!-- TODO -->
    </family>

  /**
   * Convert a GEDCOM address into an XML element
   * @param addr the GEDCOM address
   **/
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

  /**
   * Convert an individual event into an XML element. It takes a type and a
   * list, and use the first element of this type.
   * @param type_ the event type we're interested in
   * @param events the individual's events list
   **/
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
   * @param baseTag the tag used for the list. It'll be used for each inner
   * element and pluralized for the container element.
   * @param lst the string list
   **/
  def makeStringsListXML(baseTag : String,
                         lst : java.util.List[StringWithCustomTags]) =
    <xml>{
      lst.asScala.map(s => <xml>{s}</xml>.copy(label=baseTag))
    }</xml>.copy(label = baseTag + "s")

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
