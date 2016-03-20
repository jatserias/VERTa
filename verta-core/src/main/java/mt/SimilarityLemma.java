package mt;

import edu.smu.tspell.wordnet.SynsetType;


public class SimilarityLemma extends WnBaseSimilarity implements Similarity{


	public String getClassName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setReversed(boolean reversed) {}

	/**
	 * One feature Word form
	 */
	public double similarity(String[] featureNames, Word proposedWord,
			Word referenceWord) {
		
		for(SynsetType t:SynsetType.ALL_TYPES) {
		String lemmasS[] = wn.getBaseFormCandidates(proposedWord.getFeature(featureNames[0]), t);
		String lemmasT[] = wn.getBaseFormCandidates(referenceWord.getFeature(featureNames[0]), t);
		
		for(String lemmaS:lemmasS)
			for(String lemmaT:lemmasT) {
				if(lemmaT.compareTo(lemmaS)==0) return Similarity.MAXVAL;
			}
		}
		return Similarity.MINVAL;
	}
	
	

}
