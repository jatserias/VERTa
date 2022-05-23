<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml" xmlns:html="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes"/>
<xsl:template name="countsmetric">
  <xsl:param name="pgrup"/>
  <xsl:param name="metric"/>
   <xsl:value-of select="//statistics/grup[position() = $pgrup]/metric[position() = $metric]"/>
</xsl:template>
  
  
<xsl:template match="exp">
<html><head>
 </head>
 <title>Experiment</title>
<style>
  th
  {
  background-color: grey;
  color: white;
  text-align: center;
  vertical-align: bottom;
  height: 150px;
  padding-bottom: 3px;
  padding-left: 5px;
  padding-right: 5px;
  }
  
  .verticalText
  {
  text-align: center;
  vertical-align: middle;
  width: 20px;
  margin: 0px;
  padding: 0px;
  padding-left: 3px;
  padding-right: 3px;
  padding-top: 10px;
  white-space: nowrap;
  -webkit-transform: rotate(-90deg); 
  -moz-transform: rotate(-90deg);  
  };
  </style>
 <body>
 
 Experiment named <b><xsl:value-of select="name"/></b> launched at <b><xsl:value-of select="date"/></b>
 
 
 
 <h2>Configuration</h2>
 
 <b>Hypotesis File:</b><xsl:value-of select="conf/hypotesis/@filename"/> <br/>
 <b>Reference File:</b><xsl:value-of select="conf/reference/@filename"/> <br/>
 <b>MetricConf File:</b><xsl:value-of select="conf/metric/@filename"/> <br/>
 
  Weight Sum:<xsl:value-of select="/fms/@weightsum"/><br/>
  <xsl:for-each select="/fms/group">
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
 

<!-- NERC -->
  <h2>Named Entities</h2>
  <b>source</b>
 <table border="1">
  <xsl:for-each select="//nerc/src/ne">
   <tr><td><xsl:value-of select="text()"/></td><td><xsl:value-of select="@type"/></td></tr>
  </xsl:for-each>
 </table>
 <b>target</b>
  <table border="1">
 <xsl:for-each select="//nerc/trg/ne">
   <tr><td><xsl:value-of select="text()"/></td><td><xsl:value-of select="@type"/></td></tr>
  </xsl:for-each>
 </table>
 
<!-- NERC -->
  <h2>Linked Named Entities</h2>
  <b>source</b>
 <table border="1">
  <xsl:for-each select="//nel/src/ne">
   <tr><td><xsl:value-of select="text()"/></td></tr>
  </xsl:for-each>
 </table>
 <b>target</b>
  <table border="1">
 <xsl:for-each select="//nel/trg/ne">
   <tr><td><xsl:value-of select="text()"/></td></tr>
  </xsl:for-each>
 </table>

 <!-- SENTIMENT -->
 <h2>Sentiment</h2>
<table border="1">
<tr><td><b>source</b></td><td><xsl:value-of select="//senti/src/@score"/></td></tr>
<tr><td><b>target</b></td><td><xsl:value-of select="//senti/trg/@score"/></td></tr>
</table>


 <!-- TIMEX -->
  <h2>TIMEX</h2>
  <b>source</b>
 <table border="1">
  <xsl:for-each select="//timex/src/TIMEX3">
   <tr><td><xsl:value-of select="text()"/></td><td><xsl:value-of select="@value"/></td><td><xsl:value-of select="@type"/></td></tr>
  </xsl:for-each>
 </table>
 <b>target</b>
  <table border="1">
 <xsl:for-each select="//timex/trg/TIMEX3">
   <tr><td><xsl:value-of select="text()"/></td><td><xsl:value-of select="@value"/></td><td><xsl:value-of select="@type"/></td></tr>
  </xsl:for-each>
 </table>
 
 
 
 <!--  LM -->
  <h2>LM</h2>
 <table border="1">
  <tr><td>log(p(x))</td><td>exp(log(p(x))</td></tr>
   <tr><td><xsl:value-of select="//lm/score/text()"/></td><td><xsl:value-of select="//lm/escore/text()"/></td></tr>
 </table>
 
<xsl:for-each select="//presults/lexresults/align">
<h2>Align Test</h2>
  <b>Precision</b>
  <table border="1">
   <tr><td><b>source</b></td>
   <xsl:for-each select="st/s">
      <td>
      <xsl:choose><xsl:when test="@t &lt; 0"><xsl:attribute name="bgcolor">red</xsl:attribute></xsl:when></xsl:choose>
      <xsl:call-template name="sword">
        <xsl:with-param name="wid"><xsl:value-of select="@s"/></xsl:with-param>
     </xsl:call-template><sub><xsl:value-of select="position()"/></sub>
     </td>
     </xsl:for-each> 
    </tr>
    <tr><td><b>map s-t</b></td>
    <xsl:for-each select="st/s">
    <td>
    <xsl:choose>
      <xsl:when test="@t >= 0">
	<xsl:call-template name="tword">
        <xsl:with-param name="wid"><xsl:value-of select="@t"/></xsl:with-param>
     </xsl:call-template><sub><xsl:value-of select="@s + 1"/>-<xsl:value-of select="@t + 1"/></sub>
      </xsl:when>
      <xsl:otherwise>
      <span size="+1" style="background-color:red">X</span>
      </xsl:otherwise>
      </xsl:choose>
    </td> 
   </xsl:for-each> 
   </tr> 
   <tr><td><b>target</b></td>

 <xsl:for-each select="ts/s">  
    <td>
    <xsl:choose><xsl:when test="@t &lt; 0"><xsl:attribute name="bgcolor">red</xsl:attribute></xsl:when></xsl:choose>
      <xsl:call-template name="tword">
        <xsl:with-param name="wid"><xsl:value-of select="@s"/></xsl:with-param>
     </xsl:call-template><sub><xsl:value-of select="position()"/></sub>
     </td>
  </xsl:for-each>
  </tr>
   </table>
   
   <b>Recall</b>
   <table border="1">
   <tr><td><b>target</b></td>
   <xsl:for-each select="ts/s">
      <td>
      <xsl:choose><xsl:when test="@t &lt; 0"><xsl:attribute name="bgcolor">red</xsl:attribute></xsl:when></xsl:choose>
      <xsl:call-template name="tword">
        <xsl:with-param name="wid"><xsl:value-of select="@s"/></xsl:with-param>
     </xsl:call-template><sub><xsl:value-of select="position()"/></sub>
     </td>
     </xsl:for-each> 
    </tr>
   <tr><td><b>map t-s</b></td>
    <xsl:for-each select="ts/s">
    <td>
    <xsl:choose>
      <xsl:when test="@t >= 0">
	<xsl:call-template name="sword">
        <xsl:with-param name="wid"><xsl:value-of select="@t"/></xsl:with-param>
     </xsl:call-template><sub><xsl:value-of select="@s + 1"/>-<xsl:value-of select="@t + 1"/></sub>
      </xsl:when>
      <xsl:otherwise>
      <span size="+1" style="background-color:red">X</span>
      </xsl:otherwise>
      </xsl:choose>
    </td> 
   </xsl:for-each> 
   </tr>  
   <tr><td><b>source</b></td>
   <xsl:for-each select="st/s">
      <td>
      <xsl:choose><xsl:when test="@t &lt; 0"><xsl:attribute name="bgcolor">red</xsl:attribute></xsl:when></xsl:choose>
      <xsl:call-template name="sword">
        <xsl:with-param name="wid"><xsl:value-of select="@s"/></xsl:with-param>
     </xsl:call-template><sub><xsl:value-of select="position()"/></sub>
     </td>
     </xsl:for-each>
   </tr>  
  </table>
</xsl:for-each>
 
 <h2>Dist Matrix Global</h2>
<xsl:for-each select="//presults/lexresults/wmetric">
  <h3>Metric <xsl:value-of select="@name"/></h3>
  <xsl:apply-templates select="simmatrix"/>
</xsl:for-each>
 
 <!-- Dep analysis -->
   <h2>Dependency triples</h2><a name="triples"></a>

 <h3>Precision</h3> 
  <table border="1"><tr><th>#</th><th>Max score</th><th>Score</th><th>source</th><th>target</th><th>Pattern</th></tr>
  <xsl:for-each select="//trips[@type='s2t']/trip">
   <tr><td><xsl:value-of select="position()"/></td><td><xsl:value-of select="@tc"/></td><td><xsl:value-of select="@sc"/></td><td><xsl:value-of select="src"/></td><td><xsl:value-of select="trg"/></td><td><xsl:value-of select="@prov"/></td></tr>
  </xsl:for-each>
 </table>

<h3>Recall</h3>
  <table border="1"><tr><th>#</th><th>Max score</th><th>Score</th><th>source</th><th>target</th><th>Pattern</th></tr>
  <xsl:for-each select="//trips[@type='t2s']/trip">
   <tr><td><xsl:value-of select="position()"/></td><td><xsl:value-of select="@tc"/></td><td><xsl:value-of select="@sc"/></td><td><xsl:value-of select="src"/></td><td><xsl:value-of select="trg"/></td><td><xsl:value-of select="@prov"/></td></tr>
  </xsl:for-each>
 </table>

 
 <h2>Results</h2>
 Processed in <xsl:value-of select="@time"/> miliseconds  <br/>
 <b>Precision:</b><xsl:value-of select="results/prec"/><br/>
 <b>Recall:</b> <xsl:value-of select="results/rec"/><br/>



<h2>Ngrams</h2><a name="ngrams"></a>
<h3>Precision</h3>
<table border="1">
 <tr><td><b>size</b></td><td><b>#match ngrams</b></td></tr>
   <xsl:for-each select="//s2t/ngrams/resngram[@common > 0]">
         <tr><td><xsl:value-of select="@s"/></td><td><xsl:value-of select="@common"/></td></tr>
    </xsl:for-each> 
</table>        
<table border="1">
<tr>
<xsl:for-each select="//hyp/sen[1]/word">
     <td> <xsl:variable name="val" select="position() - 2"/><xsl:variable name="valm" select="position() - 1"/>
      <xsl:choose><xsl:when test="//s2t/ngrams[@s = 2]/ngram[ ( ( @s = $val ) or ( @s = $valm ) ) and @found='1']"><xsl:attribute name="bgcolor">green</xsl:attribute></xsl:when></xsl:choose>
      <xsl:value-of select="feat[@name='WORD']"/>_<xsl:value-of select="feat[@name='POS']"/>/<xsl:value-of select="feat[@name='SPOS']"/><sub><xsl:value-of select="position()"/></sub>   
     </td> 
 </xsl:for-each>        
 </tr>  
 </table>

<h3>Recall</h3>
 <table border="1">
  <tr><td><b>size</b></td><td><b>#match ngrams</b></td></tr>
   <xsl:for-each select="//t2s/ngrams/resngram[@common >0]">
         <tr><td><xsl:value-of select="@s"/></td><td><xsl:value-of select="@common"/></td></tr>
    </xsl:for-each> 
</table>
<table border="1">
        <tr>
<xsl:for-each select="//ref/sen[1]/word">
     <td> <xsl:variable name="val" select="position() - 2"/><xsl:variable name="valm" select="position() - 1"/>
      <xsl:choose><xsl:when test="//t2s/ngrams[@s = 2]/ngram[ ( ( @s = $val ) or ( @s = $valm ) ) and @found='1']"><xsl:attribute name="bgcolor">green</xsl:attribute></xsl:when></xsl:choose>
      <xsl:value-of select="feat[@name='WORD']"/>_<xsl:value-of select="feat[@name='POS']"/>/<xsl:value-of select="feat[@name='SPOS']"/><sub><xsl:value-of select="position()"/></sub>   
     </td> 
 </xsl:for-each>        
 </tr>  
 </table>











 
 <h2>All metrics</h2>
 
 <table border='1'>
<xsl:for-each select="//metres">
<tr><td><table border='1'>
   <tr><td> <b>Precision:</b></td><td><xsl:value-of select="prec"/></td></tr>
   <tr><td> <b>Recall:</b></td><td> <xsl:value-of select="rec"/> </td></tr>
  <tr><td>  <b>F1:</b></td><td> <xsl:value-of select="f"/> </td></tr>
</table></td>
<xsl:for-each select="wmetric">
<td> Metric <xsl:value-of select="@name"/></td><td>
 <table border='1'>
   <tr><td> <b>Precision:</b></td><td><xsl:value-of select="p"/></td></tr>
   <tr><td> <b>Recall:</b></td><td> <xsl:value-of select="r"/> </td></tr>
  <tr><td>  <b>F1:</b></td><td> <xsl:value-of select="f"/> </td></tr>
</table></td>
</xsl:for-each>
</tr>
 </xsl:for-each>
 </table>
 
 
 <h2>Word  Level Metrics</h2>

 <xsl:for-each select="//presults/lexresults/wmetric">
     Metric <xsl:value-of select="@name"/>
 <table border='1'>
   <tr><td> <b>Precision:</b></td><td><xsl:value-of select="p"/></td></tr>
  <tr><td> <b>Recall:</b></td><td> <xsl:value-of select="r"/> </td></tr>
 <tr><td>  <b>F1:</b></td><td> <xsl:value-of select="f"/> </td></tr>
 <tr><td>  <b>Weighted Precision:</b></td><td><xsl:value-of select="wp"/></td></tr>
 <tr><td>  <b>Weighted Recall:</b></td><td> <xsl:value-of select="wr"/> </td></tr>
 <tr><td> <b>Weighted F1:</b> </td><td> <xsl:value-of select="wf"/></td></tr>
</table>
</xsl:for-each>

<h2>Sentence Level Metrics</h2>
<xsl:for-each select="//presults/senresults/wmetric">
     Metric <xsl:value-of select="@name"/>
 <table border='1'>
   <tr><td> <b>Precision:</b></td><td><xsl:value-of select="p"/></td></tr>
  <tr><td> <b>Recall:</b></td><td> <xsl:value-of select="r"/> </td></tr>
 <tr><td>  <b>F1:</b></td><td> <xsl:value-of select="f"/> </td></tr>
 <tr><td>  <b>Weighted Precision:</b></td><td><xsl:value-of select="wp"/></td></tr>
 <tr><td>  <b>Weighted Recall:</b></td><td> <xsl:value-of select="wr"/> </td></tr>
 <tr><td> <b>Weighted F1:</b> </td><td> <xsl:value-of select="wf"/></td></tr>
</table>
</xsl:for-each>


<hr/>
<ul>
 <xsl:for-each select="reflink">
 <li><a><xsl:attribute name="href"><xsl:value-of select="@link"/></xsl:attribute>Reference <xsl:value-of select="position()"/></a></li>
 </xsl:for-each>
</ul>
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
       <span size="+1"><xsl:attribute name="style">background-color:<xsl:call-template name="color"><xsl:with-param name="ncolor"><xsl:value-of select="@bst"/></xsl:with-param></xsl:call-template></xsl:attribute><xsl:value-of select="@st"/></span>
      </xsl:when>
      <xsl:otherwise>
       <xsl:value-of select="@st"/>
      </xsl:otherwise>
      </xsl:choose>
       / 
      <xsl:choose>
      <xsl:when test="@bts > 0">
       <span size="+1"><xsl:attribute name="style">background-color:<xsl:call-template name="color"><xsl:with-param name="ncolor"><xsl:value-of select="@bts"/></xsl:with-param></xsl:call-template></xsl:attribute><xsl:value-of select="@ts"/></span>
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
   
  <!-- now we have several word metrics that can have several groups -->
  <xsl:for-each select="fms/group">
   <h2> Lexic Metric <xsl:value-of select="position()"/> Analysis </h2>
   
   <table><tr>
   <td> 
  <b>Color metric</b>
  <!-- Side description of color- component -->
  <table border='1'>
  <xsl:for-each select="fm">
    <tr><td><xsl:attribute name="bgcolor">
       <xsl:call-template name="color"><xsl:with-param name="ncolor"><xsl:value-of select="position()"/></xsl:with-param></xsl:call-template>
     </xsl:attribute>
     <xsl:value-of select="@class"/>.<xsl:value-of select="@name"/>:<xsl:value-of select="@weight"/>
    </td></tr>
  </xsl:for-each>
  </table>
  <!-- content of matching -->
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

<xsl:template name="color">
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

<!-- NOW we have several matrix -->
<xsl:template name="groupcolor">
<xsl:param name="groupid"/>
<!--  use to aply to all //  -->
<xsl:for-each select="../simmatrix">
<table border="1">
 <tr><td></td><xsl:for-each select="tw[1]/s"><th><div class="verticalText"><xsl:value-of select="@w"/></div></th></xsl:for-each></tr>
 
 <xsl:for-each select="tw">
   <tr>
    <td align="center"><xsl:value-of select="@w"/></td>
    
    <xsl:for-each select="ft[@type='p2t']">
     <td>
     <table>
     <xsl:for-each select="group[$groupid=position()]">
       <xsl:for-each select="mt[@active='#20B020']">
        <tr><xsl:attribute name="bgcolor">
           <xsl:call-template name="color"><xsl:with-param name="ncolor"><xsl:value-of select="@simid"/></xsl:with-param></xsl:call-template></xsl:attribute>
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
 </xsl:for-each> 
  </xsl:template> 

<xsl:template name="tword">
  <xsl:param name="wid"/>
    <xsl:value-of select="//ref/sen[1]/word[position() = $wid + 1]/feat[@name='WORD']"/>_<xsl:value-of select="//ref/sen[1]/word[position() = $wid + 1]/feat[@name='POS']"/>/<xsl:value-of select="//ref/sen[1]/word[position() = $wid + 1]/feat[@name='SPOS']"/>
</xsl:template>

<xsl:template name="sword">
  <xsl:param name="wid"/>
    <xsl:value-of select="//hyp/sen[1]/word[position() = $wid + 1]/feat[@name='WORD']"/>_<xsl:value-of select="//hyp/sen[1]/word[position() = $wid + 1]/feat[@name='POS']"/>/<xsl:value-of select="//hyp/sen[1]/word[position() = $wid + 1]/feat[@name='SPOS']"/>
</xsl:template>
</xsl:stylesheet>
