package mt;

import mt.core.BaseSimilarity;
import mt.core.Similarity;
import mt.nlp.Word;

public class SimilaritySubstring extends BaseSimilarity implements Similarity {
	public int nchars = 1;

	public SimilaritySubstring(String snchars) {
		nchars = Integer.parseInt(snchars);
	}

	public double similarity(String[] featureNames, Word proposedWord, Word referenceWord) {
		boolean hold = true;
		int i = 0;

		while (i < featureNames.length && hold) {
			String featureName = featureNames[i];
			String proposedValue = proposedWord.getFeature(featureName);
			String referenceValue = referenceWord.getFeature(featureName);
			hold = (referenceValue.substring(0, Math.min(nchars, referenceValue.length()))
					.compareTo(proposedValue.substring(0, Math.min(nchars, proposedValue.length()))) == 0);
			++i;
		}
		return hold ? MAX_VAL : MIN_VAL;
	}

	public String getClassName() {
		return SimilaritySubstring.class.getName();
	}

	public void setReversed(boolean reversed) {
	}
}
