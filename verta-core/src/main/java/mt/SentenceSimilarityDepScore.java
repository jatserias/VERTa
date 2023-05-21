package mt;

import java.io.PrintStream;

import mt.core.MetricActivationCounter;
import mt.core.ISentenceAlignment;
import mt.core.SentenceSimilarityBase;
import mt.core.SimilarityResult;
import mt.nlp.Sentence;

public class SentenceSimilarityDepScore extends SentenceSimilarityBase {

	public SentenceSimilarityDepScore(MetricActivationCounter counters) {
		super(counters);
	}

	@Override
	public SimilarityResult similarity(Sentence s1, Sentence s2, ISentenceAlignment dist, PrintStream strace) {

		if (strace != null) {
			strace.println("<depscore>");
			strace.println("<src score='" + s1.getDepscore() + "'/>");
			strace.println("<trg score='" + s2.getDepscore() + "'/>");
			strace.println("</depscore>");
		}

		double val = mt.SimilarityScores.getSym2(s1.getDepscore(), s2.getDepscore());

		return new SimilarityResult(val, val);
	}

	@Override
	public void dump(PrintStream strace) {
	}

}