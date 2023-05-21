package mt;

import java.io.PrintStream;

import mt.core.MetricActivationCounter;
import mt.core.ISentenceAlignment;
import mt.core.SentenceSimilarityBase;
import mt.core.SimilarityResult;
import mt.nlp.Sentence;

public class SentenceSimilaritySentiment extends SentenceSimilarityBase {

	public SentenceSimilaritySentiment(MetricActivationCounter counters) {
		super(counters);
	}

	@Override
	public SimilarityResult similarity(Sentence s1, Sentence s2, ISentenceAlignment dist, PrintStream strace) {

		if (strace != null) {
			strace.println("<senti>");
			strace.println("<src score='" + s1.getSentimentScore() + "'/>");
			strace.println("<trg score='" + s2.getSentimentScore() + "'/>");
			strace.println("</senti>");
		}

		double val = (4 - Math.abs(s1.getSentimentScore() - s2.getSentimentScore())) / 4;
		return new SimilarityResult(val, val);
	}

	@Override
	public void dump(PrintStream strace) {
	}

}
