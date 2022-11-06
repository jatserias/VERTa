package mt;

import edu.smu.tspell.wordnet.SynsetType;
import mt.core.Similarity;
import mt.core.WnBaseSimilarity;
import mt.nlp.Word;

public class SimilarityLemma extends WnBaseSimilarity implements Similarity {

	public String getClassName() {
		return "SimilarityLemma";
	}

	public void setReversed(boolean reversed) {
	}

	/**
	 * One feature Word form
	 */
	public double similarity(String[] featureNames, Word proposedWord, Word referenceWord) {

		for (SynsetType t : SynsetType.ALL_TYPES) {
			String lemmasS[] = wn.getBaseFormCandidates(proposedWord.getFeature(featureNames[0]), t);
			String lemmasT[] = wn.getBaseFormCandidates(referenceWord.getFeature(featureNames[0]), t);

			for (String lemmaS : lemmasS)
				for (String lemmaT : lemmasT) {
					if (lemmaT.compareTo(lemmaS) == 0)
						return Similarity.MAX_VAL;
				}
		}
		return Similarity.MIN_VAL;
	}

}
