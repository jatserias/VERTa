package mt;

import mt.core.BaseSimilarity;
import mt.core.Similarity;
import mt.nlp.Word;

public class SimilarityUnsimilar extends BaseSimilarity implements Similarity {
	@Override
	public double similarity(String[] featureNames, Word proposedWord, Word referenceWord) {
		return Similarity.MIN_VAL;
	}

	@Override
	public String getClassName() {
		return this.getClass().getName();
	}

	@Override
	public void setReversed(boolean reversed) {
	}

}
