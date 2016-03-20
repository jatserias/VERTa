package mt;

/**
 * A  function to compare two NERC 
 * @author jordiatserias
 *
 */
public class SimilarityNERC {

	static double similarity(NERC a, NERC b) {
		if(a.mention.compareTo(b.mention)==0) { return Similarity.MAXVAL; }
		return Similarity.MINVAL;
	}
}
