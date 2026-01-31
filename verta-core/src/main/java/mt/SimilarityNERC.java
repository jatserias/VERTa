package mt;

import mt.core.IFeaturesWordSimilarity;
import mt.nlp.NERC;

/// A  function to compare two NERC 
public class SimilarityNERC {

	static double similarity(NERC a, NERC b) {
		if (a.mention.compareTo(b.mention) == 0) {
			return IFeaturesWordSimilarity.MAX_VAL;
		}
		return IFeaturesWordSimilarity.MIN_VAL;
	}
}
