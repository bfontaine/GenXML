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

    def toSafeString =
      Option(s).map(s => Text(s.toString))
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
    def toXML = {
      val copyrightLines = h.copyrightData.asScala.toList
      val version = h.gedcomVersion.versionNumber
      <infos>
        { if (copyrightLines.size > 0)
            copyrightLines.mkString("\n").toXML("copyright") }
        { if (h.date) h.date.toXML("date") }
        { if (h.language) h.language.toXML("language") }
        { if (version)
            version.toString.toXML("gedcomVersion") }
      </infos>
    }
  }

  /** Individuals **/

  /** An XMLable {Individual} **/
  implicit class GIndividual(val individual : Individual) extends XMLable {

    val SEX_NOT_PROVIDED = "N"

    def parseSex(sex : StringWithCustomTags) =
      Option(sex).map(_.toString) match {
        case Some(v @ ("F" | "M" | "U")) => v
        case _ => SEX_NOT_PROVIDED
      }

    def toXML =
      <individual id={individual.xref.toStringId}
                  sex={parseSex(individual.sex)}>
        { individual.names.toXML }
        { individual.events.toXML }
        { if (individual.wwwUrls) individual.wwwUrls.toXML("url") }
        { individual.notes.toXML }
        { individual.familiesWhereChild.asScala.toList match {
            case fam :: _ =>
              <familyWhereChild xref={fam.family.xref.toStringId} />
            case _ =>
              null
          }
        }
        { individual.familiesWhereSpouse.toXML }
        { if (individual.multimedia) individual.multimedia.toXML }
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
        { if (family.events) family.events.toXML }
        { if (family.multimedia) family.multimedia.toXML }
      </family>
  }

  /** An XMLable {Family} map **/
  implicit class GenFamiliesList(val fams : JMap[String, Family]) extends XMLable {
    def toXML =
      <families>
        { fams.asScala.toMap.map(_._2.toXML) }
      </families>
  }

  /** An XMLable {FamilySpouse} list **/
  implicit class GenFamiliesWhereSpouse(val fams : JList[FamilySpouse]) extends XMLable {
    def toXML =
      <familiesWhereSpouse>
        { fams.asScala.toList.map(_.toXML) }
      </familiesWhereSpouse>
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
      <event type={ev.`type`.title}
             date={ev.date.toSafeString}
             cause={ev.cause.toSafeString}
             place={if (ev.place) Option(Text(ev.place.placeName)) else None}/>
  }

  /** An XMLable {FamilyEvent} **/
  implicit class GFamilyEvent(val ev : FamilyEvent) extends XMLable {
    def toXML =
      <event type={ev.`type`.title}
             date={ev.date.toSafeString}
             place={if (ev.place) Option(Text(ev.place.placeName)) else None}/>
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

  /** Multimedia **/

  implicit class GMultimedias(val medias : JList[Multimedia]) extends XMLable {
    def toXML =
      <medias>
        { medias.asScala.toList.map(_.toXML) }
      </medias>
  }

  implicit class GMultimedia(val media : Multimedia) extends XMLable {
    def toXML =
      <media id={media.xref.toSafeString}>
        <title>{media.embeddedTitle}</title>
        { if (media.fileReferences) media.fileReferences.toXML }
        { if (media.notes) media.notes.toXML }
      </media>
  }

  implicit class GFileReferences(val refs : JList[FileReference]) extends XMLable {
    def toXML =
      <files>
        { refs.asScala.toList.map(_.toXML) }
      </files>
  }

  implicit class GFileReference(val ref : FileReference) extends XMLable {
    def toXML =
      <file format={ref.format.toSafeString}
            type={ref.mediaType.toSafeString}
            href={ref.referenceToFile.toSafeString match {
              case None => None
              case Some(Text(s)) =>
                // some bad formatted files lead to references on multiple
                // lines
                Some(Text(s.split("\n")(0)))
            }}
            title={ref.title.toSafeString match {
              // Some files errors lead to a title set to "TITL"
              case Some(Text("TITL")) => None
              case x => x
            }} />
  }

  /** Individual fields **/

  /** An XMLable {PersonalName} list **/
  implicit class GPersoNames(val names : JList[PersonalName]) extends XMLable {
    val n = names.asScala.toList
    def toXML =
      if (n.isEmpty)
        <personalName/>
      else
        // get only the first name (≠ the firstname)
        names.asScala.toList.head.toXML
  }

  /** An XMLable {PersonalName} **/
  implicit class GPersoName(val name : PersonalName) extends XMLable {
    def toXML = {
      val parts = name.basic.split("/")
      val size = parts.size
      <personalName>
      { if (size > 0 && parts(0) != "") parts(0).toXML("firstname") }
      { if (size > 1 && parts(1) != "") parts(1).toXML("lastname") }
      { if (size > 2) parts(2).toXML("title") }
      </personalName>
    }
  }

  /** Others **/

  /** An XMLable {StringWithCustomTags} **/
  implicit class GCustomString(val s : StringWithCustomTags) extends XMLCustomTag {
    override def toXML =
      <xml>{toSafeString.getOrElse("")}</xml>

    /**
     * Convert this value into one that can be safely included in an XML
     * document.
     * This is the same as {GString#toSafeString} but they can’t be merged
     **/
    def toSafeString =
      Option(s).map(s => Text(s.toString))
  }

  /** An XMLable {StringWithCustomTags} list **/
  implicit class GCustomStrings(val ss : JList[StringWithCustomTags]) extends XMLCustomTag {
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
