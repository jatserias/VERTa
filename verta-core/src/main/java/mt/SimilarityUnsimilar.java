package mt;

public class SimilarityUnsimilar extends BaseSimilarity implements Similarity {
	@Override
	public double similarity(String[] featureNames, Word proposedWord,
			Word referenceWord) {
		return Similarity.MINVAL;
	}

	@Override
	public String getClassName() {
		return this.getClass().getName();
	}

	@Override
	public void setReversed(boolean reversed) {		
	}

}
