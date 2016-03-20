package mt;

import edu.smu.tspell.wordnet.SynsetType;

public class SimilarityHypernymWnPosMFS extends SimilarityHypernymWnMFS implements  Similarity {

	private static final int FT_POST = 1;

	public SimilarityHypernymWnPosMFS(String multilevel) {
		super(multilevel);
	}
	
	@Override
	public double INNERsimilarity(String[] featureNames, Word proposedWord,Word referenceWord, SynsetType[] lpos) {
		String featPOS=featureNames[FT_POST];
		
	  if(proposedWord.getFeature(featPOS).compareTo(referenceWord.getFeature(featPOS))!=0) return Similarity.MINVAL;
			
//      if(!(proposedWord.getFeature(featPOS).toLowerCase().startsWith("n") &&
//	   referenceWord.getFeature(featPOS).toLowerCase().startsWith("n"))) return Similarity.MINVAL;
		
	  SynsetType[] ppos = wn.getSynsetTypeFromPos(proposedWord.getFeature(featPOS));
		 if(ppos==null) return Similarity.MINVAL;
      return super.INNERsimilarity(featureNames, proposedWord, referenceWord,ppos);
	}
	
	
}