package mt;

import edu.smu.tspell.wordnet.SynsetType;

public class SimilarityHyponymWnPos extends SimilarityHyponymWn implements  Similarity {

	public SimilarityHyponymWnPos(String multilevel) {
		super(multilevel);
	}
	
	@Override
	public double INNERsimilarity(String[] featureNames, Word proposedWord,	Word referenceWord, SynsetType[] lpos) {
		String featPOS=featureNames[1];
		
	  if(proposedWord.getFeature(featPOS).compareTo(referenceWord.getFeature(featPOS))!=0) return Similarity.MINVAL;
		
//      if(!(proposedWord.getFeature(featPOS).toLowerCase().startsWith("n") &&
//	   referenceWord.getFeature(featPOS).toLowerCase().startsWith("n"))) return Similarity.MINVAL;
		
	  SynsetType[] ppos = wn.getSynsetTypeFromPos(proposedWord.getFeature(featPOS));
		 if(ppos==null) return Similarity.MINVAL;
      return super.INNERsimilarity(featureNames, proposedWord, referenceWord,ppos);
	}

}
