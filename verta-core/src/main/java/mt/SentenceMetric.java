package mt;

import java.io.PrintStream;

/**
 * 
 * reads several sentence metrics and combine them
 * 
 * @author jordi
 *
 */
public interface SentenceMetric {

	public SimilarityResult similarity(final Sentence s1, final Sentence s2, final SentenceAlignment dist, PrintStream strace);

	public void dump(PrintStream strace);
			
}
