package mt;

import mt.core.Similarity;
import mt.core.WnBaseSimilarity;
import mt.nlp.Word;
import verta.wn.JABSynset;

/**
 * 
 * WordNet synonms
 * 
 * 1 if there is a common synonym 0 otherwise
 * 
 */
public class SimilaritySynonymWn extends WnBaseSimilarity implements Similarity {

	public double similarity(String[] featureNames, Word proposedWord, Word referenceWord) {

		String featureProposed = proposedWord.getFeature(featureNames[0]);
		String featureReference = referenceWord.getFeature(featureNames[0]);

		if (featureReference.equals(featureProposed))
			return Similarity.MAXVAL;

		JABSynset[] proposedSynsets = wn.getSynsets(featureProposed);
		JABSynset[] referenceSynsets = wn.getSynsets(featureReference);

		return similarity(featureProposed, featureReference, proposedSynsets, referenceSynsets);

	}

	public double similarity(String featureProposed, String featureReference, final JABSynset[] proposedSynsets,
			final JABSynset[] referenceSynsets) {
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

		return found ? Similarity.MAXVAL : Similarity.MINVAL;
	}

	public String getClassName() {
		return SimilaritySynonymWn.class.getName();
	}

	public void setReversed(boolean reversed) {
	}
}
