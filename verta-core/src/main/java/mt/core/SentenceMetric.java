package mt.core;

import java.io.PrintStream;

import mt.nlp.Sentence;

/// reads several sentence metrics and combine them
public interface SentenceMetric {

	public SimilarityResult similarity(final Sentence s1, final Sentence s2, final SentenceAlignment dist, PrintStream strace);

	public void dump(PrintStream strace);
			
}
