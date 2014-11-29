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

  implicit class StringId(val id : String) {
    private val idLimitersRegExp = "(^@|@$)".r

    def toStringId =
      idLimitersRegExp.replaceAllIn(id, "")
  }

  implicit class GenEventType(val t : IndividualEventType) {
    def title = t.display.toLowerCase
  }

  implicit class GenDocument(val g : Gedcom) extends XMLable {
    def toXML =
      <document>
        <infos></infos>
        { g.individuals.toXML }
        { g.families.toXML }
      </document>
  }

  implicit class GenIndividualsList(val indivs : JMap[String, Individual]) extends XMLable {
    def toXML =
      <individuals>
        { indivs.asScala.toMap.map(_._2.toXML) }
      </individuals>
  }

  implicit class GenFamiliesList(val fams : JMap[String, Family]) extends XMLable {
    def toXML =
      <families>
        { fams.asScala.toMap.map(_._2.toXML) }
      </families>
  }

  implicit class GenIndividual(val individual : Individual) extends XMLable {
    def toXML =
      <individual id={individual.xref.toStringId}>
        <!-- TODO name, death, etc -->
        <sex>{ individual.sex }</sex>
        { individual.events.toXML }
        { individual.emails.toXML }
        { individual.faxNumbers.toXML }
        { individual.wwwUrls.toXML }
        { individual.phoneNumbers.toXML }
        { individual.address.toXML }
        { individual.notes.toXML }
      </individual>
  }

  implicit class GenFamily(val family : Family) extends XMLable {
    def toXML = // TODO
      <family id={family.xref.toStringId}>
        <!-- TODO -->
      </family>
  }

  implicit class GenAddress(val addr : Address) extends XMLable {
    def toXML =
      if (addr != null)
        <address>
          <addr1>{ addr.addr1 }</addr1>
          <addr2>{ addr.addr2 }</addr2>
          <postalCode>{ addr.postalCode }</postalCode>
          <city>{ addr.city }</city>
          <stateOrProvince>{ addr.stateProvince }</stateOrProvince>
          <country>{ addr.country }</country>
        </address>
      else <address/>
  }

  implicit class GenIndividualEvents(val evs : JList[IndividualEvent]) extends XMLable {
    def toXML =
      <events>
        { evs.asScala.toList.map(_.toXML) }
      </events>
  }

  implicit class GenIndividualEvent(val ev : IndividualEvent) extends XMLable {
    def toXML =
      <event></event>
  }

}
