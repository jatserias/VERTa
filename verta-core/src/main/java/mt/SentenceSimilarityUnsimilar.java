package mt;

import java.io.PrintStream;

public class SentenceSimilarityUnsimilar extends SentenceSimilarityBase implements SentenceMetric {

	
	SentenceSimilarityUnsimilar(MetricActivationCounter counters) {
		super(counters);
		// TODO Auto-generated constructor stub
	}

	@Override
	public SimilarityResult similarity(Sentence s1, Sentence s2, SentenceAlignment dist,
			PrintStream strace) {
			return SimilarityResult.bad;
	}

	@Override
	public void dump(PrintStream strace) {
		strace.print("<metric name='unsimilar'/>");
	}

}
