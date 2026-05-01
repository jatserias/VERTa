package mt;

import edu.smu.tspell.wordnet.SynsetType;
import mt.core.IFeaturesWordSimilarity;
import mt.nlp.Word;
import verta.wn.IWordNet;

/// A class to apply wn hypernym similarity but taking into account PoS
public class SimilarityHypernymWnPos extends SimilarityHypernymWn {
	// this should be parametrized
	static final int FT_POS = 1;

	public SimilarityHypernymWnPos(IWordNet wn, String multilevel) {
		super(wn, multilevel);
	}

	public SimilarityHypernymWnPos(String multilevel) {
		super(multilevel);
	}
	
	@Override
	public double INNERsimilarity(String[] featureNames, Word proposedWord, Word referenceWord, SynsetType[] lpos) {
		String featPOS = featureNames[FT_POS];

		if (proposedWord.getFeature(featPOS).compareTo(referenceWord.getFeature(featPOS)) != 0)
			return IFeaturesWordSimilarity.MIN_VAL;

		SynsetType[] ppos = wn.getSynsetTypeFromPos(proposedWord.getFeature(featPOS));
		if (ppos == null)
			return IFeaturesWordSimilarity.MIN_VAL;
		return super.INNERsimilarity(featureNames, proposedWord, referenceWord, ppos);
	}

}
