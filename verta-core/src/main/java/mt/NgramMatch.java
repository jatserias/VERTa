package mt;

import java.io.PrintStream;

import lombok.extern.slf4j.Slf4j;
import mt.core.MetricActivationCounter;
import mt.core.ISentenceAlignment;
import mt.core.SentenceSimilarityBase;
import mt.core.SimilarityResult;
import mt.nlp.Ngram;
import mt.nlp.Sentence;

/**
 * 
 * brute force compare ngrams compare n grams of the same size compare(ngramS1,
 * ngramS2) = 1 sii existeix un alinacio entre ngramS1 i ngramS2
 * 
 */
@Slf4j
public class NgramMatch extends SentenceSimilarityBase {

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

		log.info("NGram configured min " + minsize + "-" + maxsize);
	}

	/**
	 * Assumes ngram length is the same
	 * 
	 * 
	 * @param source_ngram
	 * @param target_ngram
	 * @param align
	 * @return
	 */
	public static boolean compareNgramSameSize(final Ngram source_ngram, final Ngram target_ngram,
			final ISentenceAlignment align) {
		boolean ngram_match = true;
		int source_token_offset = source_ngram.getStart();
		int target_token_offset = target_ngram.getStart();
		
		// for every token in ngram check that they are align
		int ngram_token = 0;
		while (ngram_match && ngram_token < source_ngram.getSize()) {

			ngram_match = align.isAligned(
					 source_token_offset + ngram_token, 
					 target_token_offset + ngram_token
					);
			++ngram_token;
		}
		return ngram_match;
	}

	/**
	 * 
	 * find Ngram n1 in Ngram set s2 given the align
	 * 
	 * @param source_ngram
	 * @param ngram_list
	 * @param align
	 * @return
	 */
	public static int findNgram(final Ngram source_ngram, final Ngram[] ngram_list, final boolean blocked[],
			final ISentenceAlignment align) {
		int ngram = 0;
		boolean found = false;
		while (!found && ngram < ngram_list.length) {
			Ngram target_ngram = ngram_list[ngram];
			found = !blocked[ngram] && compareNgramSameSize(source_ngram, target_ngram, align);
			++ngram;
		}
		return found ? ngram - 1 : -1;
	}

	/**
	 * Compares ngram same size
	 * 
	 * @param source_ngrams
	 * @param target_ngrams
	 * @param align
	 * @param strace
	 * @return
	 */
	public static int compareNgramsSameSize(final Ngram[] source_ngrams, final Ngram[] target_ngrams,
                                            ISentenceAlignment align, PrintStream strace) {
		int matched_ngrams = 0;
		int match;
		boolean[] blocked = new boolean[target_ngrams.length];
		
		for (Ngram source_ngram : source_ngrams) {
		
			boolean found = (match = findNgram(source_ngram, target_ngrams, blocked, align)) >= 0;

			if (found) {
				blocked[match] = USE_BLOCKING;
				matched_ngrams++;
			}
			if (MTsimilarity.DUMP)
				strace.print("<ngram s='" + source_ngram.getStart() + "' l='" + source_ngram.getSize() + "' found='"+ (found ?"1" : "0") + "'/>");
		}
		return matched_ngrams;
	}

	/**
	 * compare Ngrams between minsize and maxsize
	 * 
	 * @param minsize
	 * @param maxsize
	 * @param s1
	 * @param s2
	 * @param align
	 * @param strace
	 * @return
	 */
	public static int compareNgrams(int minsize, int maxsize, final Sentence s1,
                                    final Sentence s2, final ISentenceAlignment align, PrintStream strace) {

		int nf1 = 0;
		if (minsize > s1.size() || minsize > s2.size())
			return 0;

		for (int ngramSize = Math.min(Math.min(s1.size(), s2.size()), minsize); ngramSize <= Math
				.min(Math.min(s1.size(), s2.size()), maxsize); ++ngramSize) {

			if (MTsimilarity.DUMP)
				strace.println("<ngrams s='" + ngramSize + "'>");
			Ngram[] n1 = ngram(s1, ngramSize);
			Ngram[] n2 = ngram(s2, ngramSize);
			int nfx = compareNgramsSameSize(n1, n2, align, strace);
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
	public SimilarityResult similarity(final Sentence s1, final Sentence s2, final ISentenceAlignment dist,
			PrintStream strace) {

		if (MTsimilarity.DUMP)
			strace.println("<s2t>");
		double ngramprec = NgramMatch.compareNgrams(minsize, maxsize, s1, s2, dist, strace);
		ngramprec = ngramprec / NgramMatch.sumatori(minsize, maxsize, s1.size());
		if (MTsimilarity.DUMP) {
			strace.println("</s2t>");
			strace.println("<t2s>");
		}
		
		ISentenceAlignment dist_rev = dist.revert();
		double ngramrec = NgramMatch.compareNgrams(minsize, maxsize, s2, s1, dist_rev, strace);
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
		/**
		 * returns the total number of n-grams from monsize to maxsize included
		 */
		// assert (maxsize >= size) : String.format("Ngram maxsize %d >= size %d", maxsize, size);
		int res = 0;
		for (int i = minsize; i <= Math.min(size, maxsize); i++)
			res += (size - i + 1);
		return res;
	}

	/// compare ngrams of a given size
	public static double compareNgrams(final int ngramSize,
                                       final Sentence s1,
                                       final Sentence s2,
                                       final ISentenceAlignment align, PrintStream strace) {
		
		int nf1 = 0;

		if (MTsimilarity.DUMP)
			strace.println("<ngrams s='" + ngramSize + "'>");
		
		Ngram[] n1 = ngram(s1, ngramSize);
		Ngram[] n2 = ngram(s2, ngramSize);
		int nfx = compareNgramsSameSize(n1, n2, align, strace);
		nf1 += nfx;
		
		if (MTsimilarity.DUMP) {
			strace.println("<resngram s=\"" + ngramSize + "\" common=\"" + nfx + "\"/>");
			strace.println("</ngrams>");
		}
		return nf1;
	}
}
