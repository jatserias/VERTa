package mt;

import edu.smu.tspell.wordnet.SynsetType;
import mt.nlp.Word;
import verta.wn.IWordNet;

public class SimilarityHyponymWn extends SimilarityHypernymWn {

	public SimilarityHyponymWn(String multilevel) {
		super(multilevel);
	}

	public SimilarityHyponymWn(IWordNet wn, String multilevel) {
		super(wn, multilevel);
	}

	// just use reversed and call hyperonym similarity
	public double similarity(String[] featureNames, Word proposedWord, Word referenceWord) {

		if (!reversed)
			return INNERsimilarity(featureNames, referenceWord, proposedWord, SynsetType.ALL_TYPES);
		else
			return INNERsimilarity(featureNames, proposedWord, referenceWord, SynsetType.ALL_TYPES);
	}
}
