<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="utf-8" indent="yes"/>
    <xsl:template match="/">
        <html>
            <head>
                <title>Test XHTML gedcom</title>
            </head>
            <body>
                <table border="1" cellpadding="2" cellspacing="0">
                    <caption>Individual</caption>
                    <tr>
                        <th>Identifiant</th>
                        <th>INDI_ID</th>
                        <th>Nom</th>
                        <th>Sex</th>
                        <th>emails</th>
                    </tr>
                    <xsl:apply-templates select="document/individuals/individual">
                        <xsl:sort select="@id" order="ascending"/>
                    </xsl:apply-templates>
                </table>
            </body>
        </html>
    </xsl:template>
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
            
            <!-- test if emails vide(" ") -->
            <xsl:choose>
                <xsl:when test="emails = ' '">
                    <td> - </td>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="sex"/>
                </xsl:otherwise>
            </xsl:choose>
        </tr>
    </xsl:template>
    <xsl:template match="personalName | sex | emails">
        <td><xsl:value-of select="."/></td>
    </xsl:template>
</xsl:stylesheet>
