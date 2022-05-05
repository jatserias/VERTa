package mt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import mt.core.MetricActivationCounter;
import mt.core.SentenceAlignment;
import mt.core.SentenceMetric;
import mt.core.SentenceSimilarityBase;
import mt.core.SimilarityResult;
import mt.nlp.NERC;
import mt.nlp.Sentence;
import mt.nlp.Word;
import mt.nlp.io.CONLLformat;
import mt.nlp.io.ReaderCONLL;

///  A sentence Similarity function for NERC
public class SentenceSimilarityNERC extends SentenceSimilarityBase implements SentenceMetric {

	public SentenceSimilarityNERC(MetricActivationCounter counters) {
		super(counters);
	}

	static final String NOTAG = "0";
	static final String B_TAG = "B-";
	static final String I_TAG = "I-";
	static String TFORM = "WORD";
	static String TNERC = "CONL";

	/**
	 * Extract NEs from a BIO annotations
	 */
	static public Collection<NERC> generateNERC(Sentence s) {
		Collection<NERC> res = new ArrayList<NERC>();
		// iterate through sentence and convert NERC BIO annotation into a sequence of
		// NERCs
		int nsen = 0;

		NERC cnerc = null;
		int pos = 0;

		// Extract entity mentions
		for (Word w : s) {

			String nerc = w.getFeature(TNERC);

			// current NERC end
			if (cnerc != null && !nerc.startsWith(I_TAG)) {
				cnerc.end = pos - 1;
				res.add(cnerc);
				cnerc = null;
			}

			/// consider NERC if we are not cwiki
			if (nerc.startsWith(B_TAG)) {
				cnerc = new NERC(nsen, pos, pos, w.getFeature(TFORM), nerc.substring(2));
			}

			if (cnerc != null && nerc.startsWith(I_TAG)) {
				cnerc.mention = cnerc.mention + " " + w.getFeature(TFORM);
			}

			pos++;
		}

		// Add last nerc
		if (cnerc != null) {
			cnerc.end = pos - 1;
			res.add(cnerc);
		}

		return res;

	}

	@Override
	public SimilarityResult similarity(Sentence s1, Sentence s2, SentenceAlignment dist, PrintStream strace) {

		Collection<NERC> ne1 = generateNERC(s1);
		Collection<NERC> ne2 = generateNERC(s2);

		/**
		 * compare ne1 ne2
		 */
		boolean found = false;
		int ne = 0;
		int nef = 0;
		for (NERC e1 : ne1) {

			for (NERC e2 : ne2) {
				found = (SimilarityNERC.similarity(e1, e2)) > 0;
				if (found)
					break;
			}
			if (found)
				nef++;
			ne++;
		}

		// TODO check formulas
		double prec = ne > 0 ? ((1.0 * nef) / ne) : 0.0;

		ne = 0;
		nef = 0;
		for (NERC e1 : ne2) {

			for (NERC e2 : ne1) {
				found = (SimilarityNERC.similarity(e1, e2)) > 0;
				if (found)
					break;
			}
			if (found)
				nef++;
			ne++;
		}
		double recall = ne > 0 ? ((1.0 * nef) / ne) : 0.0;

		/**
		 * trace
		 */
		if (strace != null) {
			strace.println("<nerc>");
			strace.println("<src>");

			for (NERC e1 : ne1) {
				strace.print("<ne type='" + e1.type + "'>");
				strace.println(e1.mention);
				strace.print("</ne>");
			}
			strace.println("</src>");
			strace.println("<trg>");
			for (NERC e1 : ne2) {
				strace.print("<ne type='" + e1.type + "'>");
				strace.println(e1.mention);
				strace.print("</ne>");
			}
			strace.println("</trg>");
			strace.println("</nerc>");
		}
		return new SimilarityResult(prec, recall);
	}

	@Override
	public void dump(PrintStream strace) {
	}

}
