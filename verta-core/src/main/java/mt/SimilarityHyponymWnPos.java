package mt;

import edu.smu.tspell.wordnet.SynsetType;
import mt.core.IFeaturesWordSimilarity;
import mt.nlp.Word;
import verta.wn.IWordNet;

public class SimilarityHyponymWnPos extends SimilarityHyponymWn {

	public SimilarityHyponymWnPos(String multilevel) {
		super(multilevel);
	}

	public SimilarityHyponymWnPos(IWordNet wn,String multilevel) {
		super(wn, multilevel);
	}

	@Override
	public double INNERsimilarity(String[] featureNames, Word proposedWord, Word referenceWord, SynsetType[] lpos) {
		String featPOS = featureNames[1];

		if (proposedWord.getFeature(featPOS).compareTo(referenceWord.getFeature(featPOS)) != 0)
			return IFeaturesWordSimilarity.MIN_VAL;

		SynsetType[] ppos = wn.getSynsetTypeFromPos(proposedWord.getFeature(featPOS));
		if (ppos == null)
			return IFeaturesWordSimilarity.MIN_VAL;
		return super.INNERsimilarity(featureNames, proposedWord, referenceWord, ppos);
	}

}
