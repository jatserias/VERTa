package mt;

import edu.smu.tspell.wordnet.SynsetType;
import mt.nlp.Word;

public class SimilarityHyponymWnMFS extends SimilarityHypernymWnMFS {

	public SimilarityHyponymWnMFS(String multilevel) {
		super(multilevel);
	}

	// just use reversed and call hyperonym similarity
	public double similarity(String[] featureNames, Word proposedWord, Word referenceWord) {

		if (!reversed)
			return INNERsimilarity(featureNames, referenceWord, proposedWord, SynsetType.ALL_TYPES);
		else
			return INNERsimilarity(featureNames, proposedWord, referenceWord, SynsetType.ALL_TYPES);
	}
}
