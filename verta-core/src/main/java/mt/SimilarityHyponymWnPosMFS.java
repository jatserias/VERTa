package mt;

import edu.smu.tspell.wordnet.SynsetType;
import lombok.NoArgsConstructor;
import mt.core.IFeaturesWordSimilarity;
import mt.nlp.Word;
import verta.wn.IWordNet;

@NoArgsConstructor
public class SimilarityHyponymWnPosMFS extends SimilarityHyponymWnMFS {

	private static final int FT_POS = 1;

	public SimilarityHyponymWnPosMFS(IWordNet wn, String multilevel) {
		super(wn, multilevel);
	}

	public SimilarityHyponymWnPosMFS(String multilevel) {
		super(multilevel);
	}

	@Override
	public double INNERsimilarity(String[] featureNames, Word proposedWord, Word referenceWord, SynsetType[] lpos) {
		String featPos = featureNames[FT_POS];

		if (proposedWord.getFeature(featPos).compareTo(referenceWord.getFeature(featPos)) != 0)
			return IFeaturesWordSimilarity.MIN_VAL;

		SynsetType[] ppos = wn.getSynsetTypeFromPos(proposedWord.getFeature(featPos));
		if (ppos == null)
			return IFeaturesWordSimilarity.MIN_VAL;
		return super.INNERsimilarity(featureNames, proposedWord, referenceWord, ppos);
	}

}
