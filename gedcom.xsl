<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
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
                        <th>Nom</th>
                        <th>Sex</th>
                    </tr>
                    <xsl:for-each select="document/individuals/individual">
                        <xsl:sort select="personalName" order="ascending"/>
                        <tr>
                            <td><xsl:number value="position()" format="1"/></td>
                            <td><xsl:value-of select="personalName"/></td>
                            <td><xsl:value-of select="sex"/></td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
