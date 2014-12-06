<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="utf-8" indent="yes"/>

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
        <!-- TODO <xsl:apply-templates select="infos"/> -->
        <xsl:apply-templates select="individuals"/>
        <xsl:apply-templates select="families"/>
    </xsl:template>

    <!-- Individuals -->
    <xsl:template match="individuals">
        <table border="1" cellpadding="2" cellspacing="0">
            <caption>Individual</caption>
            <tr>
                <th>Identifiant</th>
                <th>INDI_ID</th>
                <th>Nom</th>
                <th>Sex</th>
            </tr>
            <xsl:apply-templates select="individual">
                <xsl:sort select="@id" order="ascending"/>
            </xsl:apply-templates>
        </table>
    </xsl:template>

    <!-- Families -->
    <xsl:template match="families">
        <table border="1" cellpadding="2" cellspacing="0">
            <caption>Families</caption>
            <tr>
                <th>Identifiant</th>
                <th>Husband</th>
                <th>Wife</th>
                <th>Child</th>
            </tr>
            <xsl:apply-templates select="family">
                <xsl:sort select="@id" order="ascending"/>
            </xsl:apply-templates>
        </table>
    </xsl:template>

    <!-- Individual -->
    <xsl:template match="individual">
        <tr>
            <!-- individual counter-->
            <td><xsl:number value="position()" format="1"/></td>

            <!-- individual attribute -->
            <td><xsl:value-of select="@id"/></td>

            <!-- individual attribute -->
            <xsl:apply-templates select="personalName"/>

            <!-- test if sex exist -->
            <xsl:choose>
                <xsl:when test="sex">
                    <xsl:apply-templates select="sex"/>
                </xsl:when>
                <xsl:otherwise>
                    <td> - </td>
                </xsl:otherwise>
            </xsl:choose>
        </tr>
    </xsl:template>

    <!-- Individual -->
    <xsl:template match="family">
        <tr>
            <td><xsl:value-of select="@id"/></td>
            <td><xsl:value-of select="husband/@xref"/></td>
            <td><xsl:value-of select="wife/@xref"/></td>
            <td>
            <xsl:for-each select="child">
                <xsl:value-of select="@xref"/>
            </xsl:for-each>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="personalName | sex | emails">
        <td><xsl:value-of select="."/></td>
    </xsl:template>
</xsl:stylesheet>
