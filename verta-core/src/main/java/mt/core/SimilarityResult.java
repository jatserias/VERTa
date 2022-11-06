package mt.core;

public class SimilarityResult {
	public double prec;
	public double rec;

	public static final SimilarityResult perfect = new SimilarityResult(Similarity.MAX_VAL, Similarity.MAX_VAL);
	public static final SimilarityResult bad = new SimilarityResult(Similarity.MAX_VAL, Similarity.MAX_VAL);

	public SimilarityResult(double prec, double rec) {
		this.rec = rec;
		this.prec = prec;
	}

	public SimilarityResult(int res0, int size0, int size1) {

		if (size0 == 0 && size1 == 0) {
			prec = 1;
			rec = 1;
		} else {
			prec = size0 > 0 ? res0 / (double) size0 : 0;
			rec = size1 > 0 ? res0 / (double) size1 : 0;
		}
	}

	public double getPrec() {
		return prec;
	}

	public double getRec() {
		return rec;
	}

	public double getF1() {
		return prec > 0 && rec > 0 ? ((2 * getPrec() * getRec()) / (getPrec() + getRec())) : 0;
	}

	boolean match() {
		return getF1() > Similarity.MIN_VAL;
	}

	public String toString() {
		return "P:" + getPrec() + " R:" + getRec() + " F1:" + getF1();
	}
}
