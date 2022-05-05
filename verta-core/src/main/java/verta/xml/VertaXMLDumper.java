package verta.xml;

import java.io.PrintStream;

import mt.WordMetric;
import mt.core.MetricResult;
import mt.nlp.Sentence;

public class VertaXMLDumper {
	
	public boolean DUMP;
	public PrintStream strace;
	
	public VertaXMLDumper() {
		DUMP = false;
	}
	
	public void xml_dump_distances(final Sentence referenceSentence, final Sentence proposedSentence, WordMetric iwm) {
		if (DUMP) {
			// @TODO HACK recalculate all distances...
			MTmetricXMLDumper.dumpAllDist(strace, proposedSentence, referenceSentence, iwm);
		}
	}

	public  void end_sentence_metrics(MetricResult tres) {
		if (DUMP) {
			strace.println("</senresults>");
			tres.dump(strace);
			strace.println("</presults>");
		}
	}

	public  void star_sentence_metrics() {
		if (DUMP)
			strace.println("<senresults>");
	}

	public  void end_lexical_metrics() {
		if (DUMP)
			strace.println("</lexresults>");
	}

	public void start_lexical_metrics() {
		if (DUMP) {
			strace.println("<presults>");
			strace.println("<lexresults>");
		}
	}
}
