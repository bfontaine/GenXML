<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="latin1" indent="yes"/>

    <xsl:key name="indi" match="document/individuals/individual" use="@id"/>
    <xsl:key name="fam" match="document/families/family" use="@id"/>

    <!-- match racine-->
    <xsl:template match="/">
        <html>
            <head>
                <title>Gedcom XHTML</title>
            </head>
            <body>
                <xsl:apply-templates select="document"/>
            </body>
        </html>
    </xsl:template>

    <!-- Document-->
    <xsl:template match="document">
        <xsl:apply-templates select="infos"/>
        <br/>
        <xsl:apply-templates select="families"/>
        <br/>
        <xsl:apply-templates select="individuals"/>
    </xsl:template>


    <xsl:template match="infos">
        <h2> Information sur le document gedcom </h2>
        <p> <bold>Date : </bold> <xsl:value-of select="date"/></p>
        <p> <bold>Version gedcom : </bold> <xsl:value-of select="date"/></p> 
    </xsl:template>

    <!-- Families -->
    <xsl:template match="families">
        <h2>Families</h2>
        <table border="1" cellpadding="2" cellspacing="0">
            <caption>Family</caption>
            <tr>
                <th>Identifiant</th>
                <th>Mari</th>
                <th>Femme</th>
                <th>Enfant</th>
            </tr>
            <xsl:apply-templates select="family">
                <xsl:sort select="@id" order="ascending"/>
            </xsl:apply-templates>
        </table>
    </xsl:template>
    
    <!-- family -->
    <xsl:template match="family">
        <tr>
            <td><xsl:number value="position()" format="1"/></td>
            <td><xsl:apply-templates select="husband"/></td>
            <td><xsl:apply-templates select="wife"/></td>
            <td>
                <ul>
                    <xsl:for-each select="child">
                        <li>
                            <xsl:apply-templates select="."/>
                        </li>
                    </xsl:for-each>
                </ul>
            </td>
        </tr>
    </xsl:template>
    
    <!-- Family attributes (husband, wife, child) -->
    <xsl:template match="husband | wife | child">
        <xsl:choose>
            <xsl:when test=".">
                <xsl:element name="a">
                    <xsl:attribute name="href">#<xsl:value-of select="@xref"/></xsl:attribute>
                    <xsl:value-of select="key('indi',@xref)/personalName"/>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                 -
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- Individuals -->
    <xsl:template match="individuals">
            <h2>Individual</h2>
            <xsl:apply-templates select="individual">
                <xsl:sort select="@id" order="ascending"/>
            </xsl:apply-templates>
    </xsl:template>

    <!-- Individual -->
    <xsl:template match="individual">
        <hr size="3"/>    

        <!-- individual attribute -->
        <xsl:element name="h3">
            <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
            <xsl:value-of select="personalName"/>
        </xsl:element>

        <!-- individual sex -->
        <p><bold>Sexe :</bold>
            <xsl:choose>
                <xsl:when test="@sex = 'M'"> &#9794; </xsl:when>
                <xsl:when test="@sex = 'F'"> &#9792; </xsl:when>
                <xsl:when test="@sex = 'U'"> Inconnu </xsl:when>
                <xsl:when test="@sex = 'N'"> - </xsl:when>
                <xsl:otherwise> - </xsl:otherwise>
            </xsl:choose>
        </p>
        
        <!-- individual events -->
        <xsl:apply-templates select="events"/>

        <!-- individual urls -->
        <!-- individual notes -->
        <!-- individual familiesWhereChild -->
        <xsl:apply-templates select="familiesWhereChild/familyWhereChild"/>
        <!-- individual familiesWhereSpouse -->
    </xsl:template>
    
    <xsl:template match="familyWhereChild">
        <p>fils/fille de
            <!-- father link -->
            <xsl:apply-templates select="key('fam',@xref)/husband"/>
            et
            <!-- mother link -->
            <xsl:apply-templates select="key('fam',@xref)/wife"/>
        </p>

    </xsl:template>
    
    <xsl:template match="events">
        <ul>
            <xsl:apply-templates select="event">
                <xsl:sort select="type" order="ascending"/>
            </xsl:apply-templates>
        </ul>
    </xsl:template>

    <xsl:template match="event">
        <li> 
            <xsl:choose>
                <xsl:when test="@type = 'birth'">
                    Née le 
                </xsl:when>
                <xsl:when test="@type = 'death'">
                    Mort le
                </xsl:when>
                <xsl:when test="@type = 'marriage'">
                    Marié le
                </xsl:when>
                <xsl:otherwise>
                    - 
                </xsl:otherwise>
            </xsl:choose>
            <xsl:value-of select="@date"/> à
            <xsl:value-of select="@place"/>
        </li>
    </xsl:template>
    
    <xsl:template match="@date | @place | @cause">
        <xsl:choose>
            <xsl:when test=".">
                <xsl:value-of select="."/>
            </xsl:when>
            <xsl:otherwise>
                 -
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="firstname | lastname | date | gedcomVersion">
        <xsl:choose>
            <xsl:when test=".">
                <xsl:value-of select="."/>
            </xsl:when>
            <xsl:otherwise>
                 -
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


</xsl:stylesheet>
