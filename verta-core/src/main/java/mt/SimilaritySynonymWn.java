package mt;

import mt.core.Similarity;
import mt.core.WnBaseSimilarity;
import mt.nlp.Word;
import verta.wn.ISynset;

/**
 * 
 * WordNet synonyms
 * 
 * 1 if there is a common synonym 0 otherwise
 * 
 */
public class SimilaritySynonymWn extends WnBaseSimilarity implements Similarity {

	public double similarity(String[] featureNames, Word proposedWord, Word referenceWord) {

		String featureProposed = proposedWord.getFeature(featureNames[0]);
		String featureReference = referenceWord.getFeature(featureNames[0]);

		if (featureReference.equals(featureProposed))
			return Similarity.MAX_VAL;

		ISynset[] proposedSynsets = wn.getSynsets(featureProposed);
		ISynset[] referenceSynsets = wn.getSynsets(featureReference);

		return similarity(featureProposed, featureReference, proposedSynsets, referenceSynsets);

	}

	public double similarity(String featureProposed, String featureReference, final ISynset[] proposedSynsets,
			final ISynset[] referenceSynsets) {
		int i = 0;
		boolean found = false;
		while (!found && i < proposedSynsets.length) {
			int j = 0;
			while (!found && j < referenceSynsets.length) {
				found = (proposedSynsets[i].equals(referenceSynsets[j]));
				++j;
			}
			++i;
		}

		return found ? Similarity.MAX_VAL : Similarity.MIN_VAL;
	}

	public String getClassName() {
		return SimilaritySynonymWn.class.getName();
	}

	public void setReversed(boolean reversed) {
	}
}
