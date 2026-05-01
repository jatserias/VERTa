package mt;

import java.io.PrintStream;

import mt.core.MetricActivationCounter;
import mt.core.ISentenceAlignment;
import mt.core.SentenceSimilarityBase;
import mt.core.SimilarityResult;
import mt.nlp.Sentence;

/**
 * Always return perfect result
 */
public class SentenceSimilarityPerfect extends SentenceSimilarityBase {

	public SentenceSimilarityPerfect(MetricActivationCounter counters) {
		super(counters);
	}

	@Override
	public SimilarityResult similarity(final Sentence source, final Sentence target, ISentenceAlignment dist,
			PrintStream strace) {
		return SimilarityResult.perfect;
	}

	@Override
	public void dump(PrintStream strace) {
		strace.print("<metric name='perfect'/>");
	}

}
