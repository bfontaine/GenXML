package org.genxml

import java.util.{Map => JMap, List => JList}

import scala.collection.JavaConverters._
import scala.language.implicitConversions
import scala.xml._

import org.gedcom4j.parser.GedcomParser
import org.gedcom4j.model._
import org.gedcom4j.model.IndividualEventType._

import GedcomConverters._

object GedcomConverters {

  trait XMLable {
    def toXML : Elem
  }

  trait XMLCustomTag extends XMLable {
    def toXML(tag : String) : Elem =
      (this.toXML : Elem).copy(label=tag)
  }

  /** Helpers **/

  implicit class StringId(val id : String) {
    private val idLimitersRegExp = "(^@|@$)".r

    def toStringId =
      idLimitersRegExp.replaceAllIn(id, "")
  }

  /** Document **/

  implicit class GDocument(val g : Gedcom) extends XMLable {
    def toXML =
      <document>
        <infos>TODO</infos>
        { g.individuals.toXML }
        { g.families.toXML }
      </document>
  }

  /** Individuals **/

  implicit class GIndividual(val individual : Individual) extends XMLable {
    def toXML =
      <individual id={individual.xref.toStringId}>
        <!-- TODO name, death, etc -->
        <sex>{ individual.sex }</sex>
        { individual.events.toXML }
        { individual.emails.toXML("email") }
        { individual.faxNumbers.toXML("faxNumber") }
        { individual.wwwUrls.toXML("url") }
        { individual.phoneNumbers.toXML("phone") }
        { if (individual.address != null) individual.address.toXML }
        { individual.notes.toXML }
      </individual>
  }

  implicit class GenIndividualsList(val indivs : JMap[String, Individual]) extends XMLable {
    def toXML =
      <individuals>
        { indivs.asScala.toMap.map(_._2.toXML) }
      </individuals>
  }

  /** Families **/

  implicit class GFamily(val family : Family) extends XMLable {
    def toXML = // TODO
      <family id={family.xref.toStringId}>
        <!-- TODO -->
      </family>
  }

  implicit class GenFamiliesList(val fams : JMap[String, Family]) extends XMLable {
    def toXML =
      <families>
        { fams.asScala.toMap.map(_._2.toXML) }
      </families>
  }

  /** Events **/

  implicit class GIndividualEvent(val ev : IndividualEvent) extends XMLable {
    def toXML =
      <event type={ev.`type`.title}>
          <date>{ev.date}</date>
          { if (ev.cause != null) <cause>{ev.cause}</cause> }
          { if (ev.place != null) <place>{ev.place.placeName}</place> }
          <!-- TODO -->
      </event>
  }

  implicit class GIndividualEventType(val t : IndividualEventType) {
    def title = t.display.toLowerCase
  }

  implicit class GenIndividualEvents(val evs : JList[IndividualEvent]) extends XMLable {
    def toXML =
      <events>
        { evs.asScala.toList.map(_.toXML) }
      </events>
  }

  /** Notes **/

  implicit class GNote(val note : Note) extends XMLable {
    def toXML =
      <note>{ note.lines.asScala.mkString("\n") }</note>
  }

  implicit class GNotes(val notes : JList[Note]) extends XMLable {
    def toXML =
      <notes>
        { notes.asScala.map(_.toString) }
      </notes>
  }

  /** Individual fields **/

  implicit class GAddress(val addr : Address) extends XMLable {
    def toXML =
      <address>
        <addr1>{ addr.addr1 }</addr1>
        <addr2>{ addr.addr2 }</addr2>
        <postalCode>{ addr.postalCode }</postalCode>
        <city>{ addr.city }</city>
        <stateOrProvince>{ addr.stateProvince }</stateOrProvince>
        <country>{ addr.country }</country>
      </address>
  }

  /** Others **/

  implicit class GCustomString(val s : StringWithCustomTags) extends
  XMLCustomTag {
    def toXML =
      <xml>{s}</xml>
  }

  implicit class GCustomStrings(val ss : JList[StringWithCustomTags]) extends
  XMLCustomTag {
    def toXML =
      this.toXML("string")

    /**
     * List(StringWithCustomTags("A"), * StringWithCustomTags("B")).toXML("X")
     * =>
     *  <Xs>
     *    <X>A</X>
     *    <X>B</X>
     *  </Xs>
     **/
    override def toXML(tag : String) =
      <xml>
        { ss.asScala.toList.map(_.toXML(tag)) }
      </xml>.copy(label=tag+"s")
  }

}
