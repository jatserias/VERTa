package mt;

import mt.core.Similarity;
import mt.nlp.NERC;

/// A  function to compare two NERC 
public class SimilarityNERC {

	static double similarity(NERC a, NERC b) {
		if (a.mention.compareTo(b.mention) == 0) {
			return Similarity.MAX_VAL;
		}
		return Similarity.MIN_VAL;
	}
}
