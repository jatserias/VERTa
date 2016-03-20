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
<html><head>
<!--  <link	rel='stylesheet' type='text/css' href='wnss.css'/> -->
 </head>
 <title>Experiment</title>
 <body>
 
 Experiment named <b><xsl:value-of select="name"/></b> launched at <b><xsl:value-of select="date"/></b>
 
 
 
 <h2>Configuration</h2>
 
 <b>Hypotesis File:</b><xsl:value-of select="conf/hypotesis/@filename"/> <br/>
 <b>Reference File:</b><xsl:value-of select="conf/reference/@filename"/> <br/>
 <b>MetricConf File:</b><xsl:value-of select="conf/metric/@filename"/> <br/>
 
  Weight Sum:<xsl:value-of select="//fms/@weightsum"/><br/>
  <xsl:for-each select="//fms/group">
  <h2>Metrica <xsl:value-of select="position()"/></h2>
  <table border="1">
   <tr><td><b>Pes</b></td><td><b>Distancia</b></td><td><b>#aplicat</b></td></tr>
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
 
 
 
 <!-- Sentence Style -->
 <h2>Hypotesis Sentence</h2>
 <xsl:apply-templates select="hyp/sen"/>
 <h2>Reference Sentence</h2>
 <xsl:apply-templates select="ref/sen"/>
 
 <h2>Dist Matrix Global</h2>
 <xsl:apply-templates select="//simmatrix"/>
 
 <!-- Dep analysis -->
  <h2>Dependency triples</h2>
  
  <table border="1">
  <xsl:for-each select="//trips[@type='s2t']/trip">
   <tr><td><xsl:value-of select="@sc"/></td><td><xsl:value-of select="src"/></td><td><xsl:value-of select="trg"/></td></tr>
  </xsl:for-each>
 </table>
  <table border="1">
  <xsl:for-each select="//trips[@type='t2s']/trip">
   <tr><td><xsl:value-of select="@sc"/></td><td><xsl:value-of select="src"/></td><td><xsl:value-of select="trg"/></td></tr>
  </xsl:for-each>
 </table>
 
 <!-- NERC -->
  <h2>Named Entities</h2>
 <table border="1">
  <xsl:for-each select="//nerc/src/ne">
   <tr><td><xsl:value-of select="text()"/></td></tr>
  </xsl:for-each>
 </table>
  <table border="1">
 <xsl:for-each select="//nerc/trg/ne">
   <tr><td><xsl:value-of select="text()"/></td></tr>
  </xsl:for-each>
 </table>
 
 <!-- TIMEX -->
  <h2>TIMEX</h2>
 <table border="1">
  <xsl:for-each select="//timex/src/TIMEX3">
   <tr><td><xsl:value-of select="text()"/></td></tr>
  </xsl:for-each>
 </table>
  <table border="1">
 <xsl:for-each select="//timex/trg/TIMEX3">
   <tr><td><xsl:value-of select="text()"/></td></tr>
  </xsl:for-each>
 </table>
 
 
 <!--  LM -->
  <h2>LM</h2>
 <table border="1">
   <tr><td>log(p(x))</td><td>exp(log(p(x))</td></tr>
   <tr><td><xsl:value-of select="//lm/score/text()"/></td><td><xsl:value-of select="//lm/escore/text()"/></td></tr>
 
 </table>
  
 
 <h2>Results</h2>
 Processed in <xsl:value-of select="@time"/> miliseconds  <br/>
 <b>Precision:</b><xsl:value-of select="results/prec"/><br/>
 <b>Recall:</b> <xsl:value-of select="results/rec"/><br/>
 <h2>Lexic</h2>
 <b>Precision:</b><xsl:value-of select="presults/lexprec"/><br/>
 <b>Recall:</b> <xsl:value-of select="presults/lexrec"/><br/>
 <h2>Ngrams</h2>
 <b>Precision:</b><xsl:value-of select="presults/ngramprec"/><br/>
 <b>Recall:</b> <xsl:value-of select="presults/ngramrec"/><br/>
 <h2>Dep</h2>
 <b>Precision:</b><xsl:value-of select="presults/deppre"/><br/>
 <b>Recall:</b> <xsl:value-of select="presults/deprec"/><br/>
 
 <hr/>
 <xsl:for-each select="next">
 <a><xsl:attribute name="href"><xsl:value-of select="@link"/></xsl:attribute>Next Sentence</a>
 </xsl:for-each>
 </body></html>
</xsl:template>

<!--
Calculate a similarity metric per group and total
-->
 <xsl:template match="simmatrix">
 <table border="1">
 <tr><td></td><xsl:for-each select="tw[1]/s"><td align="center"><xsl:value-of select="@w"/></td></xsl:for-each></tr>
 
 <xsl:for-each select="tw">
   <tr><td align="center"><xsl:value-of select="@w"/></td>
   <xsl:for-each select="s">
     <td align="center">
     <xsl:choose>
      <xsl:when test="@bst > 0">
       <span size="+1"><xsl:attribute name="style">background-color:<xsl:call-template name="jabcolor"><xsl:with-param name="ncolor"><xsl:value-of select="@bst"/></xsl:with-param></xsl:call-template></xsl:attribute><xsl:value-of select="@st"/></span>
      </xsl:when>
      <xsl:otherwise>
       <xsl:value-of select="@st"/>
      </xsl:otherwise>
      </xsl:choose>
       / 
      <xsl:choose>
      <xsl:when test="@bts > 0">
       <span size="+1"><xsl:attribute name="style">background-color:<xsl:call-template name="jabcolor"><xsl:with-param name="ncolor"><xsl:value-of select="@bts"/></xsl:with-param></xsl:call-template></xsl:attribute><xsl:value-of select="@ts"/></span>
      </xsl:when>
      <xsl:otherwise>
       <xsl:value-of select="@ts"/>
      </xsl:otherwise>
      </xsl:choose>
     </td>
    </xsl:for-each>
   </tr>
 </xsl:for-each>
 
 </table>
 
  <!-- detailed table per metric-->
   

  <xsl:for-each select="//fms/group">
   <h2> Lexic Metric <xsl:value-of select="position()"/>Analysis </h2>
   
   <table><tr>
   <td> 
  <b>Color metric</b>
  <table border='1'>
  <xsl:for-each select="fm">
    <tr><td><xsl:attribute name="bgcolor">
       <xsl:call-template name="jabcolor"><xsl:with-param name="ncolor"><xsl:value-of select="position()"/></xsl:with-param></xsl:call-template>
     </xsl:attribute>
     <xsl:value-of select="@class"/>.<xsl:value-of select="@name"/>:<xsl:value-of select="@weight"/>
    </td></tr>
  </xsl:for-each>
  </table>
  </td><td>
  <xsl:call-template name="groupcolor">
   <xsl:with-param name="groupid"><xsl:value-of select="position()"/></xsl:with-param>
  </xsl:call-template>
  </td>
  </tr>
  </table>
 </xsl:for-each> 

 
 <!-- detailed table -->
 <h2> Precision analysis </h2>
 <table border="1">
 <tr><td></td><xsl:for-each select="tw[1]/s"><td align="center"><xsl:value-of select="@w"/></td></xsl:for-each></tr>
 
 <xsl:for-each select="tw">
   <tr><td align="center"><xsl:value-of select="@w"/></td>
    
   <xsl:for-each select="ft[@type='p2t']">
    <td><table>
    <xsl:for-each select="group">
     <xsl:for-each select="mt">
       <tr><xsl:attribute name="bgcolor"><xsl:value-of select="@active"/></xsl:attribute>
       <td align="center">
        <xsl:value-of select="@feat"/>.<xsl:value-of select="@sim"/>(<xsl:value-of select="@pword"/>, <xsl:value-of select="@rword"/>)=<xsl:value-of select="@weight"/>
        </td>
     </tr></xsl:for-each>
     <tr></tr>
     </xsl:for-each>
    </table></td>
     </xsl:for-each>
     
   </tr>
 </xsl:for-each>
 
 </table>
 
  <h2> Recall analysis</h2>
 <table border="1">
 <tr><td></td><xsl:for-each select="tw[1]/s"><td align="center"><xsl:value-of select="@w"/></td></xsl:for-each></tr>
 
 <xsl:for-each select="tw">
   <tr><td align="center"><xsl:value-of select="@w"/></td>
    
   <xsl:for-each select="ft[@type='t2p']">
    <td><table>
    <xsl:for-each select="group">
     <xsl:for-each select="mt">
       <tr><xsl:attribute name="bgcolor"><xsl:value-of select="@active"/></xsl:attribute><td align="center"><xsl:value-of select="@feat"/>.<xsl:value-of select="@sim"/>(<xsl:value-of select="@pword"/>, <xsl:value-of select="@rword"/>)=<xsl:value-of select="@weight"/></td>
     </tr></xsl:for-each>
     <tr></tr>
     </xsl:for-each>
    </table></td>
     </xsl:for-each>
     
   </tr>
 </xsl:for-each>
 
 </table>
</xsl:template>

<xsl:template name="jabcolor">
<xsl:param name="ncolor"/>
<xsl:choose>
<xsl:when test="$ncolor=1">red</xsl:when>
<xsl:when test="$ncolor=2">green</xsl:when>
<xsl:when test="$ncolor=3">orange</xsl:when>
<xsl:when test="$ncolor=4">purple</xsl:when>
<xsl:when test="$ncolor=5">cyan</xsl:when>
<xsl:when test="$ncolor=6">grey</xsl:when>
<xsl:when test="$ncolor=7">gold</xsl:when>
<xsl:when test="$ncolor=8">lightgreen</xsl:when>
<xsl:when test="$ncolor=9">yellow</xsl:when>
<xsl:otherwise>blue</xsl:otherwise>
</xsl:choose>
</xsl:template>


<xsl:template match="word">
<td><table><xsl:apply-templates select="feat"/></table></td>
</xsl:template>

<xsl:template match="feat">
<tr><td><xsl:value-of select="@name"/></td><td><xsl:value-of select="text()"/></td></tr>
</xsl:template>

<xsl:template match="sen">
 <table border="1">
 <tr>
  <xsl:apply-templates select="word"/>
 </tr>
 </table>
</xsl:template>

<xsl:template match="statistics">
<xsl:for-each select="grup">
<table>
<tr><td><xsl:value-of select="text()"/></td></tr>
</table>
</xsl:for-each>
</xsl:template>

<xsl:template name="groupcolor">
<xsl:param name="groupid"/>

<table border="1">
 <tr><td></td><xsl:for-each select="//simmatrix/tw[1]/s"><td align="center"><xsl:value-of select="@w"/></td></xsl:for-each></tr>
 
 <xsl:for-each select="//simmatrix/tw">
   <tr>
    <td align="center"><xsl:value-of select="@w"/></td>
    
    <xsl:for-each select="ft[@type='p2t']">
     <td>
     <table>
     <xsl:for-each select="group[$groupid=position()]">
       <xsl:for-each select="mt[@active='#20B020']">
        <tr><xsl:attribute name="bgcolor">
           <xsl:call-template name="jabcolor"><xsl:with-param name="ncolor"><xsl:value-of select="@simid"/></xsl:with-param></xsl:call-template></xsl:attribute>
        <td align="center">
         <xsl:value-of select="@weight"/>
        </td>
        </tr>
        </xsl:for-each>
     <tr></tr>
     </xsl:for-each>
     </table>
     </td>
     </xsl:for-each>
   </tr>
  </xsl:for-each> 
   </table>
  </xsl:template> 
</xsl:stylesheet>
