package mt;

import java.io.PrintStream;

import mt.core.MetricActivationCounter;
import mt.core.ISentenceAlignment;
import mt.core.SentenceSimilarityBase;
import mt.core.SimilarityResult;
import mt.nlp.Sentence;

public class SentenceSimilarityUnsimilar extends SentenceSimilarityBase {

	SentenceSimilarityUnsimilar(MetricActivationCounter counters) {
		super(counters);
	}

	@Override
	public SimilarityResult similarity(Sentence s1, Sentence s2, ISentenceAlignment dist, PrintStream strace) {
		return SimilarityResult.bad;
	}

	@Override
	public void dump(PrintStream strace) {
		strace.print("<metric name='unsimilar'/>");
	}

}
