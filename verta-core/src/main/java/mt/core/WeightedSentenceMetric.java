package mt.core;

import java.io.PrintStream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mt.nlp.Sentence;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WeightedSentenceMetric implements ISentenceMetric {

	private String name;
	private double weight;
	private ISentenceMetric metric;

	@Override
	public SimilarityResult similarity(final Sentence source, final Sentence target, final ISentenceAlignment dist,
			PrintStream strace) {
		return metric.similarity(source, target, dist, strace);
	}

	public void dump(PrintStream strace) {
		metric.dump(strace);
	}
}
