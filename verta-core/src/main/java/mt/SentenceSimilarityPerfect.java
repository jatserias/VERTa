package mt;

import java.io.PrintStream;

public class SentenceSimilarityPerfect extends SentenceSimilarityBase implements SentenceMetric {

	public	SentenceSimilarityPerfect(MetricActivationCounter counters) {
		super(counters);
		// TODO Auto-generated constructor stub
	}

	@Override
	public SimilarityResult similarity(final Sentence s1, final Sentence s2, SentenceAlignment dist,PrintStream strace) {
		return SimilarityResult.perfect;
	}

	@Override
	public void dump(PrintStream strace) {
		strace.print("<metric name='perfect'/>");
	}

	

}
