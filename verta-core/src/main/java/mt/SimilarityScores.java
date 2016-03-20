package mt;

public class SimilarityScores extends BaseSimilarity implements Similarity {

	@Override
	public double similarity(
			String[] featureNames, 
			Word proposedWord,
			Word referenceWord) {
		// note: only works if MINVAL is o0
		double score=MINVAL;
		
		for(String f :  featureNames) {
			   double sc1 = Double.parseDouble(referenceWord.getFeature(f));
			   double sc2 = Double.parseDouble(proposedWord.getFeature(f));
			   score += getSym(sc1, sc2);
			}
		return score/featureNames.length;
	}

	public static double getSym(double sc1, double sc2) {
		  double nsc1 = ScoreNormalizer.sigmoid(sc1);
		   double nsc2 = ScoreNormalizer.sigmoid(sc2);
		   return Math.abs(nsc2-nsc1);
	}

	public static double getSym2(double sc1, double sc2) {
		
		   return ScoreNormalizer.sigmoid(Math.abs(sc2-sc1));
	}

	@Override
	public String getClassName() {
		return SimilarityScores.class.getName();
	}

	@Override
	public void setReversed(boolean reversed) {
	}

}
