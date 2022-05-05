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
	public SimilarityResult similarity(final Sentence s1, final Sentence s2, final SentenceAlignment dist,
			PrintStream strace) {

		double ngramprec = 0.0;
		double ngramrec = 0.0;
		if (MTsimilarity.DUMP)
			strace.println("<s2t>");
		if (minsize > s1.size() || minsize > s2.size())
			return SimilarityResult.bad;

		for (int s = Math.min(Math.min(minsize, s1.size()), s2.size()); s <= Math.min(Math.min(maxsize, s1.size()),
				s2.size()); ++s) {
			ngramprec += NgramMatch.compareNgrams(s, false, s1, s2, dist, strace)
					/ NgramMatchPro.totalngrams(s, s1.size());
		}
		if (MTsimilarity.DUMP) {
			strace.println("</s2t>");
			strace.println("<t2s>");
		}
		for (int s = Math.min(Math.min(minsize, s1.size()), s2.size()); s <= Math.min(Math.min(maxsize, s1.size()),
				s2.size()); ++s) {
			// we will need to reverese dist??
			ngramrec += NgramMatch.compareNgrams(s, true, s2, s1, dist, strace)
					/ NgramMatchPro.totalngrams(s, s2.size());
		}

		if (MTsimilarity.DUMP)
			strace.println("</t2s>");
		// returning results
		int maxsize1 = Math.min(maxsize, s1.size());
		int maxsize2 = Math.min(maxsize, s2.size());
		int minsize1 = Math.min(minsize, s1.size());
		int minsize2 = Math.min(minsize, s2.size());

		return new SimilarityResult(ngramprec == 0 ? 0.0 : ngramprec / (maxsize1 - minsize1 + 1),
				ngramrec == 0 ? 0.0 : ngramrec / (maxsize2 - minsize2 + 1));

	}

	public static double totalngrams(int ngram_size, int sentence_size) {
		assert sentence_size >= ngram_size : "Ngram size can not be bigger than sentence_size";
		return (sentence_size - ngram_size + 1);
	}

}
