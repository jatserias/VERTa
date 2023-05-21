package mt;

import java.io.PrintStream;

import mt.core.MetricActivationCounter;
import mt.core.ISentenceAlignment;
import mt.core.SentenceSimilarityBase;
import mt.core.SimilarityResult;
import mt.nlp.Sentence;

public class SentenceSimilarityPerfect extends SentenceSimilarityBase {

	public SentenceSimilarityPerfect(MetricActivationCounter counters) {
		super(counters);
	}

	@Override
	public SimilarityResult similarity(final Sentence s1, final Sentence s2, ISentenceAlignment dist,
			PrintStream strace) {
		return SimilarityResult.perfect;
	}

	@Override
	public void dump(PrintStream strace) {
		strace.print("<metric name='perfect'/>");
	}

}
