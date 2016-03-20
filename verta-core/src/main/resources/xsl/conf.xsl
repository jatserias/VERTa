<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml" xmlns:html="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" indent="yes"/>
<xsl:template name="countsmetric">
  <xsl:param name="pgrup"/>
  <xsl:param name="metric"/>
  <!-- <xsl:value-of select="$pgrup"/>:<xsl:value-of select="$metric"/>:-->
   <xsl:value-of select="//statistics/grup[position() = $pgrup]/metric[position() = $metric]"/>
</xsl:template>

<xsl:template match="exp">
<html><head>VERTa exp <xsl:value-of select="name"/>:<xsl:value-of select="date"/></head>
 <title>VERTa Experiment</title>
 <body> 
 Experiment named <b><xsl:value-of select="name"/></b> launched at <b><xsl:value-of select="date"/></b>
 
 
 <h2>Configuration</h2>
 
 <b>Hypotesis File:</b><xsl:value-of select="conf/hypotesis/@filename"/> <br/>
 <b>Reference File:</b><xsl:value-of select="conf/reference/@filename"/> <br/>
 <b>MetricConf File:</b><xsl:value-of select="conf/metric/@filename"/> <br/>
 
  Weight Sum:<xsl:value-of select="//fms/@weightsum"/><br/>
  <xsl:for-each select="//fms/group">
  <h2>Word Metric <xsl:value-of select="position()"/></h2>
  <table border="1">
   <tr><td><b>Weight</b></td><td><b>Distance</b></td><td><b>#aplicat</b></td></tr>
  <xsl:for-each select="fm">
  <tr><td><xsl:value-of select="@weight"/></td><td><xsl:value-of select="@class"/><xsl:value-of select="@name"/></td>
  <td>
   <xsl:call-template name="countsmetric">
     <xsl:with-param name="pgrup"><xsl:value-of select="count(../preceding-sibling::*) + 1"/></xsl:with-param>
     <xsl:with-param name="metric"><xsl:value-of select="position()"/></xsl:with-param>
  </xsl:call-template>
  </td>
  </tr>
  </xsl:for-each>
  </table>
 </xsl:for-each>

<xsl:for-each select="//metric">
  <h2>Sentence Metric <xsl:value-of select="position()"/>: <xsl:value-of select="@name"/></h2>
</xsl:for-each>  
</body>
</html>
</xsl:template> 

</xsl:stylesheet>


