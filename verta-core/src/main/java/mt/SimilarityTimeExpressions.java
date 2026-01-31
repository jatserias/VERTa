package mt;

import mt.core.IFeaturesWordSimilarity;
import mt.nlp.TimeExpressions;

public class SimilarityTimeExpressions {

	public static double similarity(TimeExpressions e1, TimeExpressions e2) {
		return e1.getDate().compareTo(e2.getDate()) == 0 ? IFeaturesWordSimilarity.MAX_VAL : IFeaturesWordSimilarity.MIN_VAL;
	}

}
