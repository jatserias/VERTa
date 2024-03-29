package mt.core;

import java.io.PrintStream;

import mt.MTsimilarity;
import mt.NgramMatch;
import mt.nlp.Sentence;

/// A different Ngram normalization
public class NgramMatchPro extends NgramMatch {

	public NgramMatchPro(MetricActivationCounter counters, String maxsize) {
		super(counters, maxsize);
	}

	public NgramMatchPro(MetricActivationCounter counters, String minsize, String maxsize) {
		super(counters, minsize, maxsize);
	}

	@Override
	public SimilarityResult similarity(final Sentence s1, final Sentence s2, final ISentenceAlignment dist,
			PrintStream strace) {

		double nGramPrec;
		double nGramRec;
		
		if (MTsimilarity.DUMP)
			strace.println("<s2t>");
		
		if (minsize > s1.size() || minsize > s2.size())
			return SimilarityResult.bad;

		nGramPrec = getNgramNormalized(s1, s2, dist, strace);
		
		if (MTsimilarity.DUMP) {
			strace.println("</s2t>");
			strace.println("<t2s>");
		}
		
		ISentenceAlignment dist_rev = dist.revert();
		nGramRec = getNgramNormalized(s2, s1, dist_rev, strace);
	

		if (MTsimilarity.DUMP)
			strace.println("</t2s>");
		// returning results
		int maxsize1 = Math.min(maxsize, s1.size());
		int maxsize2 = Math.min(maxsize, s2.size());
		int minsize1 = Math.min(minsize, s1.size());
		int minsize2 = Math.min(minsize, s2.size());

		return new SimilarityResult(nGramPrec == 0 ? 0.0 : nGramPrec / (maxsize1 - minsize1 + 1),
				nGramRec == 0 ? 0.0 : nGramRec / (maxsize2 - minsize2 + 1));

	}

	private double getNgramNormalized(final Sentence s1, final Sentence s2, final ISentenceAlignment dist, PrintStream strace) {
		double ngramprec = 0.0;
		for (int ngram_size = Math.min(Math.min(minsize, s1.size()), s2.size()); 
				 ngram_size <= Math.min(Math.min(maxsize, s1.size()),s2.size()); 
				 ++ngram_size) {
			ngramprec += NgramMatch.compareNgrams(ngram_size, s1, s2, dist, strace)
					/ NgramMatchPro.totalngrams(ngram_size, s1.size());
		}
		return ngramprec;
	}

	public static double totalngrams(int ngram_size, int sentence_size) {
		assert sentence_size >= ngram_size : "Ngram size can not be bigger than sentence_size";
		return (sentence_size - ngram_size + 1);
	}

}
