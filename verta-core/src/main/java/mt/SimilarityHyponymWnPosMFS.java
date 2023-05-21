package mt;

import edu.smu.tspell.wordnet.SynsetType;
import mt.core.Similarity;
import mt.nlp.Word;

public class SimilarityHyponymWnPosMFS extends SimilarityHyponymWnMFS {

	private static final int FT_POS = 1;

	public SimilarityHyponymWnPosMFS(String multilevel) {
		super(multilevel);
	}

	@Override
	public double INNERsimilarity(String[] featureNames, Word proposedWord, Word referenceWord, SynsetType[] lpos) {
		String featPos = featureNames[FT_POS];

		if (proposedWord.getFeature(featPos).compareTo(referenceWord.getFeature(featPos)) != 0)
			return Similarity.MIN_VAL;

		SynsetType[] ppos = wn.getSynsetTypeFromPos(proposedWord.getFeature(featPos));
		if (ppos == null)
			return Similarity.MIN_VAL;
		return super.INNERsimilarity(featureNames, proposedWord, referenceWord, ppos);
	}

}
