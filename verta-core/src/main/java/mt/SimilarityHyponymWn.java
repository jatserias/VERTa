package mt;

import edu.smu.tspell.wordnet.SynsetType;

public class SimilarityHyponymWn extends SimilarityHypernymWn implements Similarity {

	public SimilarityHyponymWn(String multilevel) {
		super(multilevel);
		// TODO Auto-generated constructor stub
	}

	// just use reversed  and call hyperym similariry
	public double similarity(String[] featureNames, Word proposedWord,
			Word referenceWord) {
		
		  if(!reversed)  return INNERsimilarity(featureNames, referenceWord, proposedWord,SynsetType.ALL_TYPES);
		  else  return INNERsimilarity(featureNames, proposedWord, referenceWord,SynsetType.ALL_TYPES);
	}
}
