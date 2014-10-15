<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml"/> 
	<xsl:template match="/">
		<html>
			<head>
				<title>Discussion Forum Home Page</title>
			</head>
			<body>
				<h1>Discussion Forum Home Page</h1>
				<h3>Please select a message board to view:</h3>
				<ul>
				<xsl:apply-templates select="discussionForumHome/messageBoard"/>
				</ul>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="messageBoard">
	  <li><a href="viewForum?id={@id}"><xsl:value-of select="@name"/></a></li>
</xsl:template>
</xsl:stylesheet>
