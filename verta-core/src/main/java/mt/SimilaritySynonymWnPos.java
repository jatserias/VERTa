package mt;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;

/**
 * 
 * Similaritat de synomims a Wordnet tenin en compte el PoS 
 * 
 * @TODO Necesita un camp de nom POS (parametre)
 * @TODO Adjectius satelit (?)
 * 
 * @author jordiatserias
 *
 */
public class SimilaritySynonymWnPos extends SimilaritySynonymWn {
	
	//TODO This should be configurable
	private static int FT_POS=1;
	private static int FT_WORD=0;

	@Override
	public double similarity(final String[] featureNames, final Word proposedWord,
			final Word referenceWord) {
		
		String featureProposed = proposedWord.getFeature(featureNames[FT_WORD]);
		String featureReference = referenceWord.getFeature(featureNames[FT_WORD]);
		
		String posProposed = proposedWord.getFeature(featureNames[FT_POS]);
		String posReference = referenceWord.getFeature(featureNames[FT_POS]);
		
		if(posProposed.compareTo(posReference)!=0) return Similarity.MINVAL;
		
		if(featureReference.equals(featureProposed) && posProposed.equals(posReference)) return Similarity.MAXVAL;
		SynsetType[] typeProposed = wn.getSynsetTypeFromPos(posProposed);
		SynsetType[] typeReference = wn.getSynsetTypeFromPos(posReference);
		
		if(typeProposed==null || typeReference==null) return Similarity.MINVAL;
		
		// since both type must be the same, just checking one of the list is enough (intersection  will be even more efficient)
		for(SynsetType s:typeReference) {
		JABSynset[] proposedSynsets=wn.getSynsets(featureProposed,s);
		JABSynset[] referenceSynsets = wn.getSynsets(featureReference,s);
	
		 double res = similarity(featureProposed, featureReference, proposedSynsets, referenceSynsets);	
		 if(res>Similarity.MINVAL) return res;
		}
		return Similarity.MINVAL;
	}


	@Override
	public String getClassName() {
		return SimilaritySynonymWnPos.class.getName();
	}

}
