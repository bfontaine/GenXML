package org.genxml

import java.util.{Map => JMap, List => JList}

import scala.collection.JavaConverters._
import scala.language.implicitConversions
import scala.xml._

import org.gedcom4j.parser.GedcomParser
import org.gedcom4j.model._

/**
 * A set of implicit converters from {gedcom4j.model} classes to XML elements
 **/
object GedcomConverters {

  /**
   * A class extending this trait must implement a `toXML` method which returns
   * an XML element representing the class instance.
   **/
  trait XMLable {
    /**
     * Return an XML element representing the current instance
     **/
    def toXML : Elem
  }

  /**
   * Like {XMLable}, but with another {toXML} method taking a custom tag name
   **/
  trait XMLCustomTag extends XMLable {
    /**
     * By default, this method calls {toXML} and modifies its XML tag with
     * the given one.
     * @param tag custom tag for the returned XML element
     **/
    def toXML(tag : String) : Elem =
      (this.toXML : Elem).copy(label=tag)
  }

  /** Helpers **/

  /**
   * A string with a {toStringId} method which removes leading and trailing
   * @'s. This is used for GEDCOM ids.
   **/
  implicit class StringId(val id : String) {
    private val idLimitersRegExp = "(^@|@$)".r

    /**
     * Return a string without leading and trailing @'s
     **/
    def toStringId =
      idLimitersRegExp.replaceAllIn(id, "")
  }

  /**
   * An implicit conversion from {Any} to a boolean to allow code like:
   *    if (foo) ...
   * instead of:
   *    if (foo != null) ...
   *
   * @param x anything
   * @return true if x is not {null}, false if not
   **/
  implicit def any2bool(x : Any) = (x != null)

  /**
   * An XMLable {String} with a generic "xml" tag
   **/
  implicit class GString(val s : String) extends XMLCustomTag {
    def toXML =
      <xml>{s}</xml>
  }

  /** Document **/

  /** An XMLable {Gedcom} document **/
  implicit class GDocument(val g : Gedcom) extends XMLable {
    def toXML =
      <document>
        { g.header.toXML }
        { g.individuals.toXML }
        { g.families.toXML }
      </document>
  }

  /** An XMLable {Header} **/
  implicit class GHeader(val h : Header) extends XMLable {
    def toXML =
      <infos>
        { h.copyrightData.asScala.toList.mkString("\n").toXML("copyright") }
        { h.date.toXML("date") }
        { h.language.toXML("language") }
        { h.gedcomVersion.versionNumber.toString.toXML("gedcomVersion") }
      </infos>
  }

  /** Individuals **/

  /** An XMLable {Individual} **/
  implicit class GIndividual(val individual : Individual) extends XMLable {

    val CUSTOM_TAGS = List("title")

    def toXML =
      <individual id={individual.xref.toStringId}>
        { individual.names.toXML }
        { if (individual.sex) <sex>{ individual.sex }</sex> }
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

  /** An XMLable {Individual} map **/
  implicit class GenIndividualsList(val indivs : JMap[String, Individual]) extends XMLable {
    def toXML =
      <individuals>
        { indivs.asScala.toMap.map(_._2.toXML) }
      </individuals>
  }

  /** Families **/

  /** An XMLable {Family} **/
  implicit class GFamily(val family : Family) extends XMLable {
    def toXML =
      <family id={family.xref.toStringId}>
        { if (family.husband)
            <husband xref={ family.husband.xref.toStringId } /> }
        { if (family.wife)
            <wife xref={ family.wife.xref.toStringId } /> }
        {
          family.children.asScala.toList.map(ch =>
              <child xref={ch.xref.toStringId} />)
        }
        { family.events.toXML }
      </family>
  }

  /** An XMLable {Family} map **/
  implicit class GenFamiliesList(val fams : JMap[String, Family]) extends XMLable {
    def toXML =
      <families>
        { fams.asScala.toMap.map(_._2.toXML) }
      </families>
  }

  /** An XMLable {FamilyChild} list **/
  implicit class GenFamiliesWhereChild(val fams : JList[FamilyChild]) extends XMLable {
    def toXML = // TODO: use only one element, given that the list contains 0..1 elements?
      <familiesWhereChild>
        { fams.asScala.toList.map(_.toXML) }
      </familiesWhereChild>
  }

  /** An XMLable {FamilySpouse} list **/
  implicit class GenFamiliesWhereSpouse(val fams : JList[FamilySpouse]) extends XMLable {
    def toXML =
      <familiesWhereSpouse>
        { fams.asScala.toList.map(_.toXML) }
      </familiesWhereSpouse>
  }

  /** An XMLable {FamilyChild} **/
  implicit class GenFamilyWhereChild(val fam : FamilyChild) extends XMLable {
    def toXML =
      <familyWhereChild xref={fam.family.xref.toStringId} />
  }

  /** An XMLable {FamilySpouse} **/
  implicit class GenFamilyWhereSpouse(val fam : FamilySpouse) extends XMLable {
    def toXML =
      <familyWhereSpouse xref={fam.family.xref.toStringId} />
  }

  /** Events **/

  /** An XMLable {IndividualEvent} **/
  implicit class GIndividualEvent(val ev : IndividualEvent) extends XMLable {
    def toXML =
      <event type={ev.`type`.title}>
          <date>{ev.date}</date>
          { if (ev.cause) <cause>{ev.cause}</cause> }
          { if (ev.place) <place>{ev.place.placeName}</place> }
      </event>
  }

  /** An XMLable {FamilyEvent} **/
  implicit class GFamilyEvent(val ev : FamilyEvent) extends XMLable {
    def toXML =
      <event type={ev.`type`.title}>
          <date>{ev.date}</date>
          { if (ev.place) <place>{ev.place.placeName}</place> }
      </event>
  }

  /** An XMLable {IndividualEventType} **/
  implicit class GIndividualEventType(val t : IndividualEventType) {
    def title = t.display.toLowerCase
  }

  /**
   * An XMLable {FamilyEventType}
   * This can't be merged with GIndividualEventType due to the types hierarchy.
   **/
  implicit class GFamilyEventType(val t : FamilyEventType) {
    def title = t.display.toLowerCase
  }

  /** An XMLable {IndividualEvent} list **/
  implicit class GIndividualEvents(val evs : JList[IndividualEvent]) extends XMLable {
    def toXML =
      <events>
        { evs.asScala.toList.map(_.toXML) }
      </events>
  }

  /**
   * An XMLable {FamilyEvent} list
   * This can't be merged with GIndividualEvents due to the types hierarchy.
   **/
  implicit class GenFamilyEvents(val evs : JList[FamilyEvent]) extends XMLable {
    def toXML =
      <events>
        { evs.asScala.toList.map(_.toXML) }
      </events>
  }

  /** Notes **/

  /** An XMLable {Note} **/
  implicit class GNote(val note : Note) extends XMLable {
    def toXML =
      <note>{ note.lines.asScala.toList.mkString("\n") }</note>
  }

  /** An XMLable {Note} list **/
  implicit class GNotes(val notes : JList[Note]) extends XMLable {
    def toXML =
      <notes>
        { notes.asScala.toList.map(_.toXML) }
      </notes>
  }

  /** Individual fields **/

  /** An XMLable {Address} **/
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

  /** An XMLable {PersonalName} list **/
  implicit class GPersoNames(val names : JList[PersonalName]) extends XMLable {
    val n = names.asScala.toList
    def toXML =
      if (n.isEmpty)
        <personalName/>
      else
        // get only the first name
        names.asScala.toList.head.toXML
  }

  /** An XMLable {PersonalName} **/
  implicit class GPersoName(val name : PersonalName) extends XMLable {
    def toXML =
      { name.basic /* TODO parse by hand */ .toXML("personalName") }
  }

  /** Others **/

  /** An XMLable {StringWithCustomTags} **/
  implicit class GCustomString(val s : StringWithCustomTags) extends XMLCustomTag {
    def toXML =
      new GString(if (s) s.toString; else "").toXML
  }

  /** An XMLable {StringWithCustomTags} list **/
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

  /** An XMLable {StringTree} **/
  implicit class GStringTree(val st : StringTree) extends XMLable {
    def toXML =
      <xml>{st.value}</xml>.copy(label=st.tag)
  }

}
