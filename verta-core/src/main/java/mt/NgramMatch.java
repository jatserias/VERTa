package mt;

import java.io.PrintStream;

import mt.core.MetricActivationCounter;
import mt.core.SentenceAlignment;
import mt.core.SentenceMetric;
import mt.core.SentenceSimilarityBase;
import mt.core.SimilarityResult;
import mt.nlp.Ngram;
import mt.nlp.Sentence;

/**
 * 
 * bruteforce compare ngrams compare n grams of the same size compare(ngramS1,
 * ngramS2) = 1 sii existeix un alinacio entre ngramS1 i ngramS2
 * 
 */
public class NgramMatch extends SentenceSimilarityBase implements SentenceMetric {

	/// block selected ngrams
	static boolean USE_BLOCKING = true;

	/// Minimum size;
	protected int minsize;

	/// Maximum size of Ngrams
	protected int maxsize;

	/**
	 * use -1 to not limit the ngram size
	 * 
	 * @param maxsize
	 */
	public NgramMatch(MetricActivationCounter counters, String maxsize) {
		super(counters);
		init("2", maxsize);
	}

	public NgramMatch(MetricActivationCounter counters, String sminsize, String smaxsize) {
		super(counters);
		init(sminsize, smaxsize);
	}

	public void init(String sminsize, String smaxsize) {

		this.minsize = Integer.parseInt(sminsize);
		this.maxsize = Integer.parseInt(smaxsize);
		if (this.maxsize == -1)
			this.maxsize = Integer.MAX_VALUE;

		if (minsize > maxsize)
			throw new RuntimeException(
					"NGram module configure with inconsistent minsize (" + minsize + ")> maxsize (" + maxsize + ")");

		System.err.println("NGram configured min " + minsize + "-" + maxsize);
	}

	/**
	 * Assumes ngram length is the same
	 * 
	 * @param reversed
	 * 
	 * @param n1
	 * @param s2
	 * @param align
	 * @return
	 */
	public static boolean compareNgramSameSize(final boolean reversed, final Ngram n1, final Ngram s2,
			final SentenceAlignment align) {
		boolean ok = true;

		int i = 0;
		while (ok && i < n1.getSize()) {
			ok = align.isAligned(reversed, i + n1.getStart(), i + s2.getStart());
			++i;
		}
		return ok;
	}

	/**
	 * 
	 * fina Ngram n1 in Ngram set s2 given the align
	 * 
	 * @param reversed
	 * @param n1
	 * @param s2
	 * @param align
	 * @return
	 */
	public static int findNgram(boolean reversed, final Ngram n1, final Ngram[] s2, final boolean blocked[],
			final SentenceAlignment align) {
		int i = 0;
		boolean found = false;
		while (!found && i < s2.length) {
			found = !blocked[i] && compareNgramSameSize(reversed, n1, s2[i], align);
			++i;
		}
		return found ? i - 1 : -1;
	}

	/**
	 * Compartes ngram same size
	 * 
	 * @param reversed
	 * @param s1
	 * @param s2
	 * @param align
	 * @param strace
	 * @return
	 */
	public static int compareNgramsSameSize(final boolean reversed, final Ngram[] s1, final Ngram[] s2,
			SentenceAlignment align, PrintStream strace) {
		// That may no be simetric if align is not simetric
		int fn1 = 0;
		int match;
		boolean blocked[] = new boolean[s2.length];
		for (Ngram n1 : s1) {
			if (MTsimilarity.DUMP)
				strace.print("<ngram s='" + n1.getStart() + "' l='" + n1.getSize() + "' found='");
			if ((match = findNgram(reversed, n1, s2, blocked, align)) >= 0) {
				blocked[match] = USE_BLOCKING;
				;
				if (MTsimilarity.DUMP)
					strace.print("1'/>");
				fn1++;
			} else {
				if (MTsimilarity.DUMP) {
					strace.print("0'/>");
				}
			}
		}

		return fn1;
	}

	/**
	 * compare Ngrams between minsize and maxsize
	 * 
	 * @param minsize
	 * @param maxsize
	 * @param reversed
	 * @param s1
	 * @param s2
	 * @param align
	 * @param strace
	 * @return
	 */
	public static int compareNgrams(int minsize, int maxsize, final boolean reversed, final Sentence s1,
			final Sentence s2, final SentenceAlignment align, PrintStream strace) {

		int nf1 = 0;
		if (minsize > s1.size() || minsize > s2.size())
			return 0;

		for (int ngramSize = Math.min(Math.min(s1.size(), s2.size()), minsize); ngramSize <= Math
				.min(Math.min(s1.size(), s2.size()), maxsize); ++ngramSize) {

			if (MTsimilarity.DUMP)
				strace.println("<ngrams s='" + ngramSize + "'>");
			Ngram[] n1 = ngram(s1, ngramSize);
			Ngram[] n2 = ngram(s2, ngramSize);
			int nfx = compareNgramsSameSize(reversed, n1, n2, align, strace);
			nf1 += nfx;
			if (MTsimilarity.DUMP) {
				strace.println("<resngram s=\"" + ngramSize + "\" common=\"" + nfx + "\"/>");
				strace.println("</ngrams>");
			}
		}
		return nf1;
	}

	public static Ngram[] ngram(final Sentence s1, int ngramSize) {
		assert ngramSize>0 : String.format("Ngram size %d needs to be > 0", ngramSize);
		// just in case
		if (s1.size() + 1 - ngramSize < 0)
			return Ngram.EMPTY_NGRAM;
		Ngram[] res = new Ngram[s1.size() + 1 - ngramSize];
		for (int i = 0; i < (s1.size() + 1 - ngramSize); ++i) {
			// ngram
			res[i] = new Ngram(i, ngramSize);
		}

		return res;
	}

	/**
	 * 
	 * This should take into account that segments can have multiple sentences
	 * 
	 * @param s1
	 * @param s2
	 * @param dist
	 * @param strace
	 * @return
	 */
	public SimilarityResult similarity(final Sentence s1, final Sentence s2, final SentenceAlignment dist,
			PrintStream strace) {

		if (MTsimilarity.DUMP)
			strace.println("<s2t>");
		double ngramprec = NgramMatch.compareNgrams(minsize, maxsize, false, s1, s2, dist, strace);
		ngramprec = ngramprec / NgramMatch.sumatori(minsize, maxsize, s1.size());
		if (MTsimilarity.DUMP) {
			strace.println("</s2t>");
			strace.println("<t2s>");
		}
		// we will need to reverese dist??
		double ngramrec = NgramMatch.compareNgrams(minsize, maxsize, true, s2, s1, dist, strace);
		ngramrec = ngramrec / NgramMatch.sumatori(minsize, maxsize, s2.size());
		if (MTsimilarity.DUMP)
			strace.println("</t2s>");

		// returning results
		return new SimilarityResult(ngramprec, ngramrec);
	}

	@Override
	public void dump(PrintStream strace) {
		strace.print("<metric name='ngram' size='" + maxsize + "'/>");
	}

	public static int sumatori(int minsize, int maxsize) {
		return sumatori(minsize,  maxsize,  maxsize);
	}
	
	public static int sumatori(int minsize, int maxsize, int size) {
		assert (maxsize >= size) : String.format("Ngram maxsize %d >= size %d", maxsize, size);
		int res = 0;
		for (int i = minsize; i <= Math.min(size, maxsize); i++)
			res += (size - i + 1);
		return res;
	}

	/// compare ngrams of a given size
	public static double compareNgrams(final int ngramSize, final boolean reversed, final Sentence s1,
			final Sentence s2, final SentenceAlignment align, PrintStream strace) {
		int nf1 = 0;

		if (MTsimilarity.DUMP)
			strace.println("<ngrams s='" + ngramSize + "'>");
		Ngram[] n1 = ngram(s1, ngramSize);
		Ngram[] n2 = ngram(s2, ngramSize);
		int nfx = compareNgramsSameSize(reversed, n1, n2, align, strace);
		nf1 += nfx;
		if (MTsimilarity.DUMP) {
			strace.println("<resngram s=\"" + ngramSize + "\" common=\"" + nfx + "\"/>");
			strace.println("</ngrams>");
		}

		return nf1;
	}
}
