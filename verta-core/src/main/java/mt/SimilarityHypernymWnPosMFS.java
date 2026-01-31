package mt;

import edu.smu.tspell.wordnet.SynsetType;
import lombok.NoArgsConstructor;
import mt.core.IFeaturesWordSimilarity;
import mt.nlp.Word;
import verta.wn.IWordNet;

@NoArgsConstructor
public class SimilarityHypernymWnPosMFS extends SimilarityHypernymWnMFS {

	private static final int FT_POST = 1;

	public SimilarityHypernymWnPosMFS(String multilevel) {
		super(multilevel);
	}

	public SimilarityHypernymWnPosMFS(IWordNet wn, String multilevel) {
		super(wn,multilevel);
	}

	@Override
	public double INNERsimilarity(String[] featureNames, Word proposedWord, Word referenceWord, SynsetType[] lpos) {
		String featPOS = featureNames[FT_POST];

		if (proposedWord.getFeature(featPOS).compareTo(referenceWord.getFeature(featPOS)) != 0)
			return IFeaturesWordSimilarity.MIN_VAL;

		SynsetType[] ppos = wn.getSynsetTypeFromPos(proposedWord.getFeature(featPOS));
		if (ppos == null)
			return IFeaturesWordSimilarity.MIN_VAL;
		return super.INNERsimilarity(featureNames, proposedWord, referenceWord, ppos);
	}

}