package verta.xml;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Vector;

import mt.WordMetric;
import mt.core.FeatureMetric;
import mt.nlp.Word;

public class WordMetricXMLDumper {

	public static void wm_dump(WordMetric wm, PrintStream trace) {
	
		trace.println("<fms weightsum=\"" + wm.groupWeight + "\">");
		for (String key : wm.featureMetrics.keySet()) {
			Vector<FeatureMetric> x = wm.featureMetrics.get(key);
			trace.println("<group>");
			for (FeatureMetric fm : x) {
				fm.dump(trace);
			}
			trace.println("</group>");
		}
		trace.println("</fms>");
	}

	public static void xml_wm_dump(final Word proposedWord, final Word targetWord, PrintStream pout, double contrib, int f,
			boolean active, FeatureMetric fm) {
		if (pout != null) {
			pout.print(
					"<mt feat=\"" + XMLFormater.encodeXMLString(Arrays.deepToString(fm.featureNames)) + "\"");
			pout.print(" sim=\"" + fm.getClassName() + "\" simid=\"" + (f + 1) + "\"");
			pout.print(" active=\"" + (active ? "#20B020" : "#B02020") + "\"");
			pout.print(" pword=\"" + XMLFormater
					.encodeXMLString(Arrays.deepToString(proposedWord.getFeatures(fm.featureNames))) + "\" ");
			pout.print(" rword=\""
					+ XMLFormater.encodeXMLString(Arrays.deepToString(targetWord.getFeatures(fm.featureNames)))
					+ "\" ");
			pout.println(" weight=\"" + contrib + "\">");
			pout.println("</mt>");
		}
	}

	public static void xml_wm_start_group(PrintStream pout, int ngroup) {
		if (pout != null)
			pout.println("<group id=\"" + ngroup + "\">");
	}

	public static void  xml_wm_end_group(PrintStream pout) {
		if (pout != null)
			pout.println("</group>");
	}

	public static void xml_wm_end_ft(PrintStream pout) {
		if (pout != null)
			pout.println("</ft>");
	}

	public static void xml_wm_start_ft(PrintStream pout, String type) {
		if (pout != null)
			pout.println("<ft type=\"" + type + "\">");
	}

}
