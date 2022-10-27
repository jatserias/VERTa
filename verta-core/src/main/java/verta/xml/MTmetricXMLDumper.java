package verta.xml;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Date;

import mt.MTsimilarity;
import mt.WordMetric;
import mt.core.DistanceMatrix;
import mt.core.MetricResult;
import mt.core.WeightedSentenceMetric;
import mt.nlp.Sentence;
import mt.nlp.Word;

public class MTmetricXMLDumper {

	public static PrintStream start_xml_dump(String metricConfigFile, String experimentName, String[] refFilenames,
			int nSystem, String hypFilename, PrintStream trace) throws FileNotFoundException {
		if (MTsimilarity.DUMP) {
	
			trace = new PrintStream(MTsimilarity.BASEFOLDER + "trace" + experimentName + "_" + nSystem + ".xml");
			
			// @TODO change the index page
			trace.println(XMLFormater.header());
			trace.println("<?xml-stylesheet type=\"text/xsl\" href=\"conf.xsl\" ?>");
			trace.println("<exp><name>" + XMLFormater.encodeXMLString(experimentName) + "</name>");
			Date d = new Date(System.currentTimeMillis());
			trace.println("<date>" + XMLFormater.encodeXMLString(d.toLocaleString()) + "</date>");
			trace.println("<conf>");
			trace.println("<hypotesis filename=\"" + XMLFormater.encodeXMLString(hypFilename) + "\"/>");
			for (String refFilename : refFilenames) {
				trace.println("<reference filename=\"" + XMLFormater.encodeXMLString(refFilename) + "\"/>");
			}
			trace.println("<metricconf filename=\"" + XMLFormater.encodeXMLString(metricConfigFile) + "\">");
		}
		return trace;
	}

	public static void printXMLHeader(String experimentName, MTsimilarity mt, Sentence proposedSentence,
			PrintStream strace, Sentence referenceSentence, String topXSL) {
		MTmetricXMLDumper.printHeader(strace, experimentName, topXSL);
		mt_dump(mt, strace);
		MTmetricXMLDumper.printXmlSentence(proposedSentence, "hyp", strace);
		MTmetricXMLDumper.printXmlSentence(referenceSentence, "ref", strace);
	}

	public static void printXmlSentence(Sentence proposedSentence, String tag, PrintStream gtrace) {
		gtrace.println("<" + tag + ">");
		Sentence.dump(proposedSentence, gtrace);
		gtrace.println("</" + tag + ">");
	}

	public static void xml_copy_files() {
		if (MTsimilarity.DUMP) {
			MTmetricXMLDumper.copyxsl("/xsl/" + MTmetricXMLDumper.TOPXSL + ".xsl", MTsimilarity.BASEFOLDER + "/" + MTmetricXMLDumper.TOPXSL + ".xsl");
			MTmetricXMLDumper.copyxsl("/xsl/conf.xsl", MTsimilarity.BASEFOLDER + "/conf.xsl");
			MTmetricXMLDumper.copyxsl("/xsl/match.xsl", MTsimilarity.BASEFOLDER + "/match.xsl");
		}
	}

	public static void xml_dump_configuration(MTsimilarity mt, PrintStream trace) {
		if (MTsimilarity.DUMP) {
			mt_dump(mt, trace);
			trace.println("</metricconf>");
			trace.println("</conf>");
		}
	}

	public static PrintStream xml_dump_sentence(String experimentName, int nSystem, MTsimilarity mt, int nseg,
			Sentence proposedSentence, int nsen, PrintStream gtrace) throws FileNotFoundException {
		if (MTsimilarity.DUMP) {
			gtrace = new PrintStream(MTsimilarity.BASEFOLDER + "trace" + experimentName + "_" + nSystem + "_s" + nseg
					+ "." + nsen + ".xml");
			MTmetricXMLDumper.printHeader(gtrace, experimentName, MTmetricXMLDumper.TOPXSL);
			mt_dump(mt, gtrace);
			printXmlSentence(proposedSentence, "hyp", gtrace);
		}
		return gtrace;
	}

	public static PrintStream xml_dump_sentence_ref(String experimentName, int nSystem, MTsimilarity mt, int nseg,
			Sentence proposedSentence, int nsen, PrintStream gtrace, PrintStream strace, int nref,
			Sentence referenceSentence) throws FileNotFoundException {
		if (MTsimilarity.DUMP) {
	
			if (strace != null) {
				strace.println("</exp>");
				strace.close();
			}
			String sname = "trace" + experimentName + "_" + nSystem + "_ref" + nref + "_s" + nseg
					+ "." + nsen + ".xml";
			gtrace.println("<reflink link=\"" + sname + "\"/>");
			strace = new PrintStream(MTsimilarity.BASEFOLDER + sname);
			printXMLHeader(experimentName, mt, proposedSentence, strace, referenceSentence, MTmetricXMLDumper.TOPXSL);
	
		}
		return strace;
	}

	public static void xml_similarity_dump(PrintStream gtrace, MetricResult res) {
		if (MTsimilarity.DUMP) {
			res.dump(gtrace);
		}
	}

	public static void xml_dump_global_results(String experimentName, int nSystem, PrintStream trace, int nseg,
			int nsen, PrintStream gtrace, MetricResult MAXRes) {
		if (MTsimilarity.DUMP) {
			trace.println("<res link=\"" + "trace" + experimentName + "_" + nSystem + "_s" + nseg + "."
					+ (nsen - 1) + ".xml" + "\"><f>" + MAXRes.getWF1() + "</f></res>");
			gtrace.println("<results>");
			gtrace.println("<f>" + MAXRes.getWF1() + "</f>");
			gtrace.println("<prec>" + MAXRes.getPrec() + "</prec>");
			gtrace.println("<rec>" + MAXRes.getRec() + "</rec>");
			gtrace.println("</results>");
		}
	}

	public static void xml_add_sentence_link(String experimentName, int nSystem, int nseg, PrintStream gtrace) {
		if (MTsimilarity.DUMP) {
			gtrace.println("<next link=\"trace" + experimentName + "_" + nSystem + "_s" + nseg
					+ ".1.xml" + "\"/>");
		}
	}

	public static void xml_close_experiment(PrintStream gtrace, PrintStream strace) {
		if (MTsimilarity.DUMP) {
	
			gtrace.println("</exp>");
			gtrace.close();
			strace.println("</exp>");
			strace.close();
		}
	}

	public static void xml_dump_statistics(MTsimilarity mt, PrintStream trace) {
		if (MTsimilarity.DUMP) {
			dumpStatistics(mt, trace);
			trace.println("</exp>");
			trace.close();
		}
	}

	public static void copyxsl(String sourceFilename, String outfilename) {
		try {
	
			BufferedReader in = new BufferedReader(
					new InputStreamReader(MTsimilarity.class.getResourceAsStream(sourceFilename))); // new
																									// FileReader(sourceFilename));
			PrintStream outfile = new PrintStream(outfilename);
	
			String buff;
			while ((buff = in.readLine()) != null) {
				outfile.println(buff);
			}
			outfile.close();
		} catch (Exception e) {
			System.err.println("Error copy XSL file from " + sourceFilename + " to " + outfilename);
			System.err.println("Check that file and destination folder exists");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static final String TOPXSL = "match";

	public static void printHeader(PrintStream trace, String experimentName, String topxsl) {
		trace.println(XMLFormater.header());
		trace.println("<?xml-stylesheet type=\"text/xsl\" href=\"" + topxsl + ".xsl\" ?>");
		trace.println("<exp><name>" + XMLFormater.encodeXMLString(experimentName) + "</name>");
		Date d = new Date(System.currentTimeMillis());
		trace.println("<date>" + XMLFormater.encodeXMLString(d.toLocaleString()) + "</date>");
	}

	// @TODO redu XML trace
	public static void dumpStatistics(MTsimilarity mt, PrintStream trace) {
		// wm.dumpStatistics(trace);
	}

	public static void mt_dump(MTsimilarity mt, PrintStream strace) {
		// dump word metrics
		for (WordMetric iwm : mt.verta.wms) {
			strace.println("<wordmetrics>");
			WordMetricXMLDumper.wm_dump(iwm, strace);
			strace.println("</wordmetrics>");
		}
	
		// dump sentence metrics
		for (WeightedSentenceMetric iwm : mt.verta.sm) {
			strace.println("<senmetrics>");
			iwm.dump(strace);
			strace.println("</senmetrics>");
		}
	}

	/**
	 * 
	 * dumps a table s1 X s2 &lt;simmatrix&gt; &lt;tw w="WORD_T"&gt;  &lt;s w="WORD_R" bst=""
	 * bts""/&gt; ...  &lt;/tw&gt;  &lt;simmatrix&gt;
	 * 
	 * bst: 1 iif bestmatch source target bts: 2 iif bestmatch target source
	 * 
	 * @param out
	 * @param proposedSentence
	 * @param targetSentence
	 * @param wm
	 */
	public static void dumpAllDist(PrintStream out, final Sentence proposedSentence, final Sentence targetSentence,
			final WordMetric wm) {
	
		int ipw = 0;
		DistanceMatrix d = new DistanceMatrix(proposedSentence, targetSentence);
	
		out.println("<simmatrix>");
		// mt dump
		WordMetricXMLDumper.wm_dump(wm, out);
		for (Word pw : proposedSentence) {
			out.println("<tw w=\"" + XMLFormater.encodeXMLString(pw.getFeature("WORD")) + "\">");
			int itw = 0;
			for (Word tw : targetSentence) {
				double simu = wm.similarity(pw, tw, out, "p2t");
				//wm.reversed = true;
				double simd = wm.similarity(tw, pw, out, "t2p");
				//wm.reversed = false;
				// @TODO we should add bestMatch info in both ways
				out.print("<s w=\"" + XMLFormater.encodeXMLString(tw.getFeature("WORD")) + "\" st=\"" + simu
						+ "\" ts=\"" + simd + "\" ");
				// we should store which is the distance used
				out.print(" bst='" + ((d.bestMatchH(ipw) == itw) ? "1" : "0") + "'");
				out.print(" bts='" + ((d.bestMatchV(itw) == ipw) ? "2" : "0") + "'");
				out.println("/>");
				++itw;
			}
			out.println("</tw>");
			++ipw;
		}
		out.println("</simmatrix>");
	}

}
