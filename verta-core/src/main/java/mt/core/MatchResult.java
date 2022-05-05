package mt.core;

public class MatchResult {
	public MatchResult(double d, Object s) {
		score = d;
		prov = s;
	}

	public double score;
	public Object prov;

	public String toString() {
		return "" + score + ":" + prov;
	}
}