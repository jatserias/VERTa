package mt;

import java.io.PrintStream;

import mt.core.MetricActivationCounter;
import mt.core.SentenceAlignment;
import mt.core.SentenceMetric;
import mt.core.SentenceSimilarityBase;
import mt.core.SimilarityResult;
import mt.nlp.Sentence;
import mt.nlp.Word;

/// SentenceSimilarity to match and count NODEP triples
public class SentenceSimilarityCountDeps extends SentenceSimilarityBase implements SentenceMetric {

	private static final String DEPFIELD = "DEPLABEL";
	String NODEP = "dep";

	public SentenceSimilarityCountDeps(MetricActivationCounter counters) {
		super(counters);
		this.NODEP = "_";
	}

	public SentenceSimilarityCountDeps(MetricActivationCounter counters, String dep) {
		super(counters);
		if (dep != null)
			this.NODEP = dep;
	}

	@Override
	public SimilarityResult similarity(Sentence s1, Sentence s2, SentenceAlignment dist, PrintStream strace) {
		// SimilarityResult res;
		int res0 = 0;

		for (Word w : s1) {
			String dep = w.getFeature(DEPFIELD);
			if (dep.compareTo(NODEP) == 0) {
				res0++;
			}
		}

		if (strace != null) {
			strace.println("<cdep>");
			strace.println("<src ndep='" + res0 + "' tdep='" + s1.size() + "'/>");
			strace.println("<trg ndep='" + res0 + "' tdep='" + s2.size() + "'/>");
			strace.println("</cdep>");
		}

		return new SimilarityResult(res0, s1.size(), s2.size());

	}

	@Override
	public void dump(PrintStream strace) {}

}
