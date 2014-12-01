package org.genxml

import java.util.{Map => JMap, List => JList}

import scala.collection.JavaConverters._
import scala.language.implicitConversions
import scala.xml._

import org.gedcom4j.parser.GedcomParser
import org.gedcom4j.model._

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

  implicit def any2bool(x : Any) = (x != null)

  /** Document **/

  implicit class GDocument(val g : Gedcom) extends XMLable {
    def toXML =
      <document>
        { g.header.toXML }
        { g.individuals.toXML }
        { g.families.toXML }
      </document>
  }

  implicit class GHeader(val h : Header) extends XMLable {
    def toXML =
      <info>
        { h.copyrightData.asScala.toList.mkString("\n").toXML("copyright") }
        { h.date.toXML("date") }
        { h.language.toXML("language") }
        { h.gedcomVersion.versionNumber.toString.toXML("gedcomVersion") }
      </info>
  }

  /** Individuals **/

  implicit class GIndividual(val individual : Individual) extends XMLable {

    val CUSTOM_TAGS = List("title")

    def toXML =
      <individual id={individual.xref.toStringId}>
        { individual.names.toXML }
        <sex>{ individual.sex }</sex>
        { individual.events.toXML }
        { if (individual.emails) individual.emails.toXML("email") }
        { if (individual.faxNumbers) individual.faxNumbers.toXML("faxNumber") }
        { if (individual.wwwUrls) individual.wwwUrls.toXML("url") }
        { if (individual.phoneNumbers) individual.phoneNumbers.toXML("phone") }
        { if (individual.address) individual.address.toXML }
        { individual.notes.toXML }
        { individual.familiesWhereChild.toXML }
        { individual.familiesWhereSpouse.toXML }
        {
          val sts = individual.customTags.asScala.toList
          sts.filter(s => CUSTOM_TAGS.contains(s.tag)).map(_.toXML)
        }
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
    def toXML =
      <family id={family.xref.toStringId}>
        { if (family.husband)
            <husband xref={ family.husband.xref.toStringId } /> }
        { if (family.wife)
            <wife xref={ family.wife.xref.toStringId } /> }
        {
          family.children.asScala.toList.map(ch =>
              <children xref={ch.xref.toStringId} />)
        }
        { family.events.toXML }
      </family>
  }

  implicit class GenFamiliesList(val fams : JMap[String, Family]) extends XMLable {
    def toXML =
      <families>
        { fams.asScala.toMap.map(_._2.toXML) }
      </families>
  }

  implicit class GenFamiliesWhereChild(val fams : JList[FamilyChild]) extends XMLable {
    def toXML =
      <familiesWhereChild>
        { fams.asScala.toList.map(_.toXML) }
      </familiesWhereChild>
  }

  implicit class GenFamiliesWhereSpouse(val fams : JList[FamilySpouse]) extends XMLable {
    def toXML =
      <familiesWhereSpouse>
        { fams.asScala.toList.map(_.toXML) }
      </familiesWhereSpouse>
  }

  class FamilyXref(val fam : Family) extends XMLable {
    def toXML =
      <family xref={fam.xref.toStringId} />
  }

  implicit class GenFamilyWhereChild(val fam : FamilyChild) extends XMLable {
    def toXML = new FamilyXref(fam.family).toXML
  }

  implicit class GenFamilyWhereSpouse(val fam : FamilySpouse) extends XMLable {
    def toXML = new FamilyXref(fam.family).toXML
  }

  /** Events **/

  implicit class GIndividualEvent(val ev : IndividualEvent) extends XMLable {
    def toXML =
      <event type={ev.`type`.title}>
          <date>{ev.date}</date>
          { if (ev.cause) <cause>{ev.cause}</cause> }
          { if (ev.place) <place>{ev.place.placeName}</place> }
      </event>
  }

  implicit class GFamilyEvent(val ev : FamilyEvent) extends XMLable {
    def toXML =
      <event type={ev.`type`.title}>
          <date>{ev.date}</date>
          { if (ev.place) <place>{ev.place.placeName}</place> }
      </event>
  }

  implicit class GIndividualEventType(val t : IndividualEventType) {
    def title = t.display.toLowerCase
  }

  // this can't be merged with GIndividualEventType due to the types hierarchy
  implicit class GFamilyEventType(val t : FamilyEventType) {
    def title = t.display.toLowerCase
  }

  implicit class GIndividualEvents(val evs : JList[IndividualEvent]) extends XMLable {
    def toXML =
      <events>
        { evs.asScala.toList.map(_.toXML) }
      </events>
  }

  // this can't be merged with GIndividualEvents due to the types hierarchy
  implicit class GenFamilyEvents(val evs : JList[FamilyEvent]) extends XMLable {
    def toXML =
      <events>
        { evs.asScala.toList.map(_.toXML) }
      </events>
  }

  /** Notes **/

  implicit class GNote(val note : Note) extends XMLable {
    def toXML =
      // FIXME isn't called?
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

  implicit class GPersoNames(val names : JList[PersonalName]) extends XMLable {
    val n = names.asScala.toList
    def toXML =
      if (n.isEmpty)
        <personalName/>
      else
        // get only the first name
        names.asScala.toList.head.toXML
  }

  implicit class GPersoName(val name : PersonalName) extends XMLable {
    def toXML =
      { name.basic /* TODO parse by hand */ .toXML("personalName") }
  }

  /** Others **/

  implicit class GCustomString(val s : StringWithCustomTags) extends
  XMLCustomTag {
    def toXML =
      new GString((if (s) s; else "").toString).toXML
  }

  implicit class GString(val s : String) extends XMLCustomTag {
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

  implicit class GStringTree(val st : StringTree) extends XMLable {
    def toXML =
      <xml>{st.value}</xml>.copy(label=st.tag)
  }

}
