package mt;

import edu.smu.tspell.wordnet.SynsetType;
import mt.core.Similarity;
import mt.nlp.Word;
import verta.wn.ISynset;

/**
 * 
 * Synonym Similarity in Wordnet using PoS
 *
 */
public class SimilaritySynonymWnPos extends SimilaritySynonymWn {

	// TODO This should be configurable
	private static int FT_POS = 1;
	private static int FT_WORD = 0;

	@Override
	public double similarity(final String[] featureNames, final Word proposedWord, final Word referenceWord) {

		String featureProposed = proposedWord.getFeature(featureNames[FT_WORD]);
		String featureReference = referenceWord.getFeature(featureNames[FT_WORD]);

		String posProposed = proposedWord.getFeature(featureNames[FT_POS]);
		String posReference = referenceWord.getFeature(featureNames[FT_POS]);

		if (posProposed.compareTo(posReference) != 0)
			return Similarity.MIN_VAL;

		if (featureReference.equals(featureProposed) && posProposed.equals(posReference))
			return Similarity.MAX_VAL;
		SynsetType[] typeProposed = wn.getSynsetTypeFromPos(posProposed);
		SynsetType[] typeReference = wn.getSynsetTypeFromPos(posReference);

		if (typeProposed == null || typeReference == null)
			return Similarity.MIN_VAL;

		// since both type must be the same, just checking one of the list is enough
		// (intersection will be even more efficient)
		for (SynsetType s : typeReference) {
			ISynset[] proposedSynsets = wn.getSynsets(featureProposed, s);
			ISynset[] referenceSynsets = wn.getSynsets(featureReference, s);

			double res = similarity(featureProposed, featureReference, proposedSynsets, referenceSynsets);
			if (res > Similarity.MIN_VAL)
				return res;
		}
		return Similarity.MIN_VAL;
	}

	@Override
	public String getClassName() {
		return SimilaritySynonymWnPos.class.getName();
	}

}
