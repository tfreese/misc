<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:db="http://docbook.org/ns/docbook"
	version="1.1">
	
	<!-- Importieren der Standard-Templates -->
	<!--
		Ohne Profiling: href="urn:docbkx:stylesheet"
		Mit Profiling: href="urn:docbkx:stylesheet/profile-docbook.xsl"
	-->
	<xsl:import href="urn:docbkx:stylesheet" />
	<xsl:import href="mystyle-common.xsl" />
	
	<!-- CSS-Datei angeben -->
	<!--xsl:param name="html.stylesheet">webhelp.css</xsl:param>	
	<xsl:param name="html.stylesheet.type">text/css</xsl:param-->	

</xsl:stylesheet>