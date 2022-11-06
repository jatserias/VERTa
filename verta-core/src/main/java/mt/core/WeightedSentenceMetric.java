package mt.core;

import java.io.PrintStream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mt.nlp.Sentence;

@Getter
@AllArgsConstructor
public class WeightedSentenceMetric implements SentenceMetric {
	String name;
	double weight;
	SentenceMetric metric;

	@Override
	public SimilarityResult similarity(final Sentence s1, final Sentence s2, final ISentenceAlignment dist,
			PrintStream strace) {
		return metric.similarity(s1, s2, dist, strace);
	}

	public void dump(PrintStream strace) {
		metric.dump(strace);
	}
}
