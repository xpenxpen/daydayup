<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ph="http://ananas.org/2003/tips/photo">

    <xsl:output method="xml" />

    <xsl:template match="ph:index">
        <html>
            <head>
                <title>
                    <xsl:value-of select="ph:title" />
                </title>
            </head>
            <xsl:apply-templates />
        </html>
    </xsl:template>

    <xsl:template match="ph:index/ph:title">
        <h1>
            <xsl:apply-templates />
        </h1>
    </xsl:template>

    <xsl:template match="ph:entry">
        <img src="{concat(.,'.jpg')}" align="right" />
        <xsl:apply-templates select="document(concat(.,'.xml'))" />
        <br clear="right" />
    </xsl:template>

    <xsl:template match="ph:photo/ph:title">
        <h2>
            <xsl:apply-templates />
        </h2>
    </xsl:template>

    <xsl:template match="ph:location">
        <h3>
            in
            <xsl:apply-templates />
        </h3>
    </xsl:template>

    <xsl:template match="ph:date">
        <p>
            Date:
            <xsl:apply-templates />
        </p>
    </xsl:template>

    <xsl:template match="ph:description">
        <p>
            <xsl:apply-templates />
        </p>
    </xsl:template>

</xsl:stylesheet>
