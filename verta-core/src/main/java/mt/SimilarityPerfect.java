package mt;

public class SimilarityPerfect extends BaseSimilarity  implements Similarity {

	@Override
	public double similarity(String[] featureNames, Word proposedWord,
			Word referenceWord) {
		return Similarity.MAXVAL;
	}

	@Override
	public String getClassName() {
		return this.getClass().getName();
	}

	@Override
	public void setReversed(boolean reversed) {		
	}


}
