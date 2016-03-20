package mt;

import edu.smu.tspell.wordnet.SynsetType;

public class SimilarityHyponymWnMFS extends SimilarityHypernymWnMFS implements Similarity {

	public SimilarityHyponymWnMFS(String multilevel) {
		super(multilevel);
	}

	// just use reversed  and call hyperym similariry
	public double similarity(String[] featureNames, Word proposedWord,
			Word referenceWord) {
		
		  if(!reversed)  return INNERsimilarity(featureNames, referenceWord, proposedWord, SynsetType.ALL_TYPES);
		  else  return INNERsimilarity(featureNames, proposedWord, referenceWord,SynsetType.ALL_TYPES);
	}
}
