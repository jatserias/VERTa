package mt;

import edu.smu.tspell.wordnet.SynsetType;

public class SimilarityHyponymWnPosMFS  extends SimilarityHyponymWnMFS implements  Similarity {

	private static final int FT_POS = 1;

	public SimilarityHyponymWnPosMFS(String multilevel) {
		super(multilevel);
	}
	
	@Override
	public double INNERsimilarity(String[] featureNames, Word proposedWord,	Word referenceWord, SynsetType[] lpos) {
		String featPos=featureNames[FT_POS];
		
		if(proposedWord.getFeature(featPos).compareTo(referenceWord.getFeature(featPos))!=0) return Similarity.MINVAL;
		
//      if(!(proposedWord.getFeature(FT_POS).toLowerCase().startsWith("n") &&
//	   referenceWord.getFeature(FT_POS).toLowerCase().startsWith("n"))) return Similarity.MINVAL;
		
		 SynsetType[] ppos = wn.getSynsetTypeFromPos(proposedWord.getFeature(featPos));
		 if(ppos==null) return Similarity.MINVAL;
      return super.INNERsimilarity(featureNames, proposedWord, referenceWord,ppos);
	}

}
