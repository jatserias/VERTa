package mt;

import mt.core.BaseSimilarity;
import mt.core.IFeaturesWordSimilarity;
import mt.nlp.Word;

public class SimilarityUnsimilar extends BaseSimilarity {
	@Override
	public double similarity(String[] featureNames, Word proposedWord, Word referenceWord) {
		return IFeaturesWordSimilarity.MIN_VAL;
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}

	@Override
	public void setReversed(boolean reversed) {
	}

}
