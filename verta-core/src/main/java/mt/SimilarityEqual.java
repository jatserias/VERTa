package mt;

import mt.core.BaseSimilarity;
import mt.core.Similarity;
import mt.nlp.Word;

public class SimilarityEqual extends BaseSimilarity implements Similarity {

	public double similarity(String[] featureNames, Word proposedWord, Word referenceWord) {
		int i = 0;
		boolean hold = true;
		while (i < featureNames.length && hold) {
			hold = (referenceWord.getFeature(featureNames[i]).compareTo(proposedWord.getFeature(featureNames[i])) == 0);
			++i;
		}
		return hold ? Similarity.MAX_VAL : Similarity.MIN_VAL;
	}

	public String getClassName() {
		return SimilarityEqual.class.getName();
	}

	public void setReversed(boolean reversed) {
	}
}
