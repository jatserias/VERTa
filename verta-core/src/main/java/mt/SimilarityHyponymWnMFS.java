package mt;

import edu.smu.tspell.wordnet.SynsetType;
import lombok.NoArgsConstructor;
import mt.nlp.Word;
import verta.wn.IWordNet;

@NoArgsConstructor
public class SimilarityHyponymWnMFS extends SimilarityHypernymWnMFS {

	public SimilarityHyponymWnMFS(IWordNet wn, String multilevel) {
		super(wn, multilevel);
	}

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
