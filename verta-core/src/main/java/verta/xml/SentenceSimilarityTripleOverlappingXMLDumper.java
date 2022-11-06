package verta.xml;

import java.io.PrintStream;
import java.util.List;

import mt.core.DistanceMatrix;
import mt.core.ISentenceAlignment;
import mt.core.TriplesMatch;
import mt.nlp.Triples;

public class SentenceSimilarityTripleOverlappingXMLDumper {

	public static void xml_dump_triples(final Triples n1, PrintStream strace, double res, Triples btrip) {
		strace.println("<trip  sc=\"" + res + "\">");
		strace.println("<src>" + XMLFormater.encodeXMLString(n1.toString()) + "</src>");
		if (btrip != null) {
			strace.println("<trg>" + XMLFormater.encodeXMLString(btrip.toString()) + "</trg>");
		} else {
			strace.println("<trg>NO MATCH</trg>");
		}
		strace.println("</trip>");
	}

public static void xml_dump_alignment(boolean reversed, TriplesMatch tmatch, ISentenceAlignment a, final List<Triples> proposedSentence,
                                      final List<Triples> referenceSentence, DistanceMatrix d, PrintStream strace, int i_align) {
		strace.println("<trips n='" + i_align + "' type=" + (reversed ? "\"t2s\">" : "\"s2t\">"));
		int i = 0;
		for (int i_al : a.getAlignment()) {
			strace.println("<trip  tc=\"" + tmatch.getWeight(proposedSentence.get(i).label) + "\" sc=\""
					+ (i_al >= 0 ? d.getDistance(i, i_al) : -1) + "\" prov=\""
					+ (i_al >= 0 ? a.getProvenance(i, i_al) : "nomatch") + "\">");
			strace.println("<src>" + XMLFormater.encodeXMLString((proposedSentence.get(i).toString())) + "</src>");
			if (i_al >= 0) {
				strace.println(
						"<trg>" + XMLFormater.encodeXMLString(referenceSentence.get(i_al).toString()) + "</trg>");
			} else {
				strace.println("<trg>NO MATCH</trg>");
			}
			strace.println("</trip>");
			i++;
		}
		strace.println("</trips>");
	}
}
