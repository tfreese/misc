<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:db="http://docbook.org/ns/docbook"
	version="1.1">

	<!-- Silbentrennung -->
	<xsl:param name="hyphenate" select="'true'" />
	
	<!-- Realtive Pfadangaben bei Bilder in Includes beibehalten -->
    <xsl:param name="keep.relative.image.uris" select="'1'" />
	
	<!-- Externer Parameter -->
	<xsl:param name="projectversion"></xsl:param>

	<!-- Erlaubt die Verwendung von <?bookversion?> im DocBook XML -->
	<xsl:template match="processing-instruction('bookversion')">
		<!-- Version wird ausgeben -->
		<xsl:value-of select="$projectversion" />
	</xsl:template>
	
	<!-- Nummerierungen -->
    <xsl:param name="appendix.autolabel" select="'A'"/>
	<xsl:param name="chapter.autolabel" select="1"/>
	<xsl:param name="part.autolabel" select="'I'"/>
	<xsl:param name="reference.autolabel" select="'I'"/>
	<xsl:param name="section.autolabel" select="1"/>
	<xsl:param name="section.label.includes.component.label" select="1"/>   
	<xsl:param name="section.autolabel.max.depth" select="3"/> 
		
	<!-- Control generation of ToCs and LoTs -->
    <xsl:param name="generate.toc">
		appendix toc,title
		article/appendix nop
		article toc,title
		book toc,title,figure,table,example,equation
		chapter title
		part toc,title
		preface toc,title
		qandadiv toc
		qandaset toc
		reference toc,title
		sect1 toc
		sect2 toc
		sect3 toc
		sect4 toc
		sect5 toc
		section toc
		set toc,title
	</xsl:param>

</xsl:stylesheet>