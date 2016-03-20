package mt;

public class SimilarityEqual extends BaseSimilarity implements Similarity {
	
	//@Override
	public double similarity(String[] featureNames ,Word proposedWord, Word referenceWord) {
		int i=0;
		boolean hold=true;
		while(i<featureNames.length && hold) {
		   hold=(referenceWord.getFeature(featureNames[i]).compareTo(proposedWord.getFeature(featureNames[i]))==0);
		   ++i;
		}
		return hold ? Similarity.MAXVAL : Similarity.MINVAL;
	}
	public String getClassName() {
		return SimilarityEqual.class.getName();
	}
	public void setReversed(boolean reversed) {}
}
