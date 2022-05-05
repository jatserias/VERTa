package mt;

import edu.smu.tspell.wordnet.SynsetType;
import mt.core.Similarity;
import mt.nlp.Word;

/// A class to apply wn hypernym similarity but taking into account PoS
public class SimilarityHypernymWnPos extends SimilarityHypernymWn implements Similarity {
	// this should be parametrized
	static final int FT_POS = 1;

	public SimilarityHypernymWnPos(String multilevel) {
		super(multilevel);
	}

	@Override
	public double INNERsimilarity(String[] featureNames, Word proposedWord, Word referenceWord, SynsetType[] lpos) {
		String featPOS = featureNames[FT_POS];

		if (proposedWord.getFeature(featPOS).compareTo(referenceWord.getFeature(featPOS)) != 0)
			return Similarity.MINVAL;

		SynsetType[] ppos = wn.getSynsetTypeFromPos(proposedWord.getFeature(featPOS));
		if (ppos == null)
			return Similarity.MINVAL;
		return super.INNERsimilarity(featureNames, proposedWord, referenceWord, ppos);
	}

}
