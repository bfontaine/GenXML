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
        <h1> Information sur le document gedcom </h1>
        <xsl:apply-templates select="infos"/>
        <br/>
        <xsl:apply-templates select="families"/>
        <br/>
        <xsl:apply-templates select="individuals"/>
    </xsl:template>

    <!-- Families -->
    <xsl:template match="families">
        <table border="1" cellpadding="2" cellspacing="0">
            <caption><h2>Families</h2></caption>
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
            <td><xsl:value-of select="@id"/></td>
            <td>
                <xsl:element name="a">
                    <xsl:attribute name="href">#<xsl:value-of select="husband/@xref"/></xsl:attribute>
                    <xsl:value-of select="key('indi',husband/@xref)/personalName"/>
                </xsl:element>
            </td>
            <td>
                <xsl:element name="a">
                    <xsl:attribute name="href">#<xsl:value-of select="wife/@xref"/></xsl:attribute>
                    <xsl:value-of select="key('indi',wife/@xref)/personalName"/>
                </xsl:element>
            </td>
            <td>
            <xsl:for-each select="child">
                (<xsl:value-of select="@xref"/>) <xsl:value-of select="key('indi',@xref)/personalName"/>
            </xsl:for-each>
            </td>
        </tr>
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

        <!-- test if sex exist -->
        <p><bold>Sexe :</bold>
            <xsl:choose>
                <xsl:when test="@sex = 'H'">
                    Homme
                </xsl:when>
                <xsl:when test="@sex = 'F'">
                    Femme
                </xsl:when>
                <xsl:when test="@sex = 'U'">
                    Inconnu <xsl:value-of select="@sex"/>
                </xsl:when>
                <xsl:when test="@sex = 'N'">
                    -
                </xsl:when>
                <xsl:otherwise>
                    - 
                </xsl:otherwise>
            </xsl:choose>
        </p>
    </xsl:template>

    <xsl:template match="firstname | lastname | emasls">
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
