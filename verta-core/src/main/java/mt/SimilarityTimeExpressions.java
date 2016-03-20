package mt;

public class SimilarityTimeExpressions {

	public static double similarity(TimeExpressions e1, TimeExpressions e2) {
		return e1.date.compareTo(e2.date)==0 ? Similarity.MAXVAL : Similarity.MINVAL;
	}

}
