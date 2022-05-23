package mt.core;

import mt.WordMetric;
import mt.nlp.Sentence;
import mt.nlp.Word;

/** Word Level Similarity (gets the Best Match from left to right)
 * 
 * THIS CLASS SEEMS NOT TO BE USED
 *
 */
@Deprecated
public class SentenceSimilarityBestMatchFromLeft {

	// TODO a patch to capture all distances
	public static double dist[][];

	/**
	 * 
	 * dist: keep all the distance between source and target
	 * 
	 * @param reversed       if true the similarity feature function is reversed
	 * @param pos            word position
	 * @param w              word
	 * @param targetSentence Sentence
	 * @param align          the
	 * @param wm             WordMetric to be applied
	 * @return the similarity [0-1]
	 */
	private static double bestMatch(boolean reversed, int pos, final Word w, final Sentence targetSentence,
			Integer[] align, WordMetric wm) {
		boolean found = false;
		double max = 0;
		int maxword = -1;
		int iw = 0;
		while (iw < targetSentence.size() && !found) {

			// the word is not taken
			if (align[iw] < 0) {

				// propagate reverse
				//wm.reversed = reversed;
				double mdist = wm.similarity(w, targetSentence.get(iw));

				// reversed on dist
				if (reversed)
					dist[iw][pos] = mdist;
				else
					dist[pos][iw] = mdist;

				// TODO store the metric info to count best-similarity usage group+metric
				if (mdist > max) {
					max = mdist;
					maxword = iw;
				}
			}
			++iw;
		}

		// update align so we do not get twice the same word
		System.err.print(w.getFeature("WORD") + " x ");
		if (maxword > -1) {
			align[maxword] = pos;
			System.err.println(targetSentence.get(maxword).getFeature("WORD") + " = " + max);
		} else {
			System.err.println("NO MATCH");
		}
		return max;
	}

	/**
	 * Calculates the similarity between two sentences
	 * 
	 * @param proposedSentence
	 * @param targetSentence
	 * @param wm               the word metric to be used
	 * @return the similarity [0-1]
	 */
	public static double sentenceSimilarity(boolean reversed, Sentence proposedSentence, Sentence targetSentence,
			WordMetric wm) {

		// initilize align no noword (-1). this is used to avoid getting the same word
		// twice
		Integer align[] = new Integer[Math.max(proposedSentence.size(), targetSentence.size())];
		for (int i = 0; i < align.length; ++i)
			align[i] = -1;

		double distBestWord[] = new double[proposedSentence.size()];
		int sw = 0;
		for (Word w : proposedSentence) {
			distBestWord[sw] = bestMatch(reversed, sw, w, targetSentence, align, wm);
			++sw;
		}

		// acomulate the results and normalize
		double sum = 0;
		for (int j = 0; j < distBestWord.length; ++j) {
			if (distBestWord[j] > 0)
				sum += distBestWord[j];
		}

		// @BUG return sum+100*sum/proposedSentence.size();
		return 100 * sum / proposedSentence.size();
	}
}
