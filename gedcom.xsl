<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="utf-8" indent="yes"/>

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
        <p> <bold>Version gedcom : </bold> <xsl:value-of select="gedcomVersion"/></p>
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
            <!-- husband -->
            <td>
                <xsl:choose>
                    <xsl:when test="husband">
                        <xsl:apply-templates select="husband"/>
                    </xsl:when>
                    <xsl:otherwise> - </xsl:otherwise>
                </xsl:choose>
            </td>
            <!-- wife -->
            <td>
                <xsl:choose>
                    <xsl:when test="wife">
                        <xsl:apply-templates select="wife"/>
                    </xsl:when>
                    <xsl:otherwise> - </xsl:otherwise>
                </xsl:choose>
            </td>
            <td>
                <xsl:choose>
                    <xsl:when test="child">
                        <ul>
                            <xsl:for-each select="child">
                                <li>
                                    <xsl:apply-templates select="."/>
                                </li>
                            </xsl:for-each>
                        </ul>
                    </xsl:when>
                    <xsl:otherwise> - </xsl:otherwise>
                </xsl:choose>
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
        <xsl:if test="familyWhereChild">
            <xsl:apply-templates select="familyWhereChild"/>
        </xsl:if>
        <!-- individual familiesWhereSpouse -->
        <xsl:if test="familiesWhereSpouse/familyWhereSpouse">
            <xsl:apply-templates select="familiesWhereSpouse"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="familyWhereChild">
        <p>fils/fille de
            <!-- father link -->
            <xsl:choose>
                <xsl:when test="key('fam',@xref)/husband"><xsl:apply-templates select="key('fam',@xref)/husband"/> </xsl:when>
                <xsl:otherwise> - </xsl:otherwise>
            </xsl:choose>
            et
            <!-- mother link -->
            <xsl:choose>
                <xsl:when test="key('fam',@xref)/wife"><xsl:apply-templates select="key('fam',@xref)/wife"/> </xsl:when>
                <xsl:otherwise> - </xsl:otherwise>
            </xsl:choose>
        </p>
    </xsl:template>

    <xsl:template match="familiesWhereSpouse">
        <table border="1" cellpadding="2" cellspacing="0">
            <caption>Enfant</caption>
            <tr>
                <th>Famille</th>
                <th>Femme</th>
                <th>Enfant</th>
            </tr>
            <xsl:apply-templates select="familyWhereSpouse">
                <xsl:sort select="." order="ascending"/>
            </xsl:apply-templates>
        </table>
    </xsl:template>

    <xsl:template match="familyWhereSpouse">
        <tr>
        <td><xsl:number value="position()" format="1"/></td>
        <!-- wife -->
        <td>
            <xsl:choose>
                <xsl:when test="key('fam',@xref)/wife">
                    <xsl:apply-templates select="key('fam',@xref)/wife"/>
                </xsl:when>
                <xsl:otherwise> - </xsl:otherwise>
            </xsl:choose>
        </td>
        <td>
            <ul>
                <xsl:for-each select="key('fam',@xref)/child">
                    <li>
                        <xsl:apply-templates select="."/>
                    </li>
                </xsl:for-each>
            </ul>
        </td>
        </tr>
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
            <!-- type event -->
            <xsl:choose>
                <xsl:when test="@type = 'birth'"> Née </xsl:when>
                <xsl:when test="@type = 'death'"> Mort </xsl:when>
                <xsl:when test="@type = 'marriage'"> Marié </xsl:when>
                <xsl:otherwise> - </xsl:otherwise>
            </xsl:choose>
            <!-- date event -->
            le
            <xsl:choose>
                <xsl:when test="@date"> <xsl:value-of select="@date"/> </xsl:when>
                <xsl:otherwise> - </xsl:otherwise>
            </xsl:choose>
            <!-- place event -->
            à
            <xsl:choose>
                <xsl:when test="@place"> <xsl:value-of select="@place"/> </xsl:when>
                <xsl:otherwise> - </xsl:otherwise>
            </xsl:choose>
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
