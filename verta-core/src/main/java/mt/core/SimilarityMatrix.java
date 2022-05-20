package mt.core;

public class SimilarityMatrix {

	protected static final String NO_PROVENANCE = "NULL";

	/// distance from source to target words
	protected double[][] dist;
	protected Object[][] prov;
	
	
	public SimilarityMatrix(double[][] m) {
		dist = m;
		prov = new Object[m.length][m[0].length];
	}
	
	public SimilarityMatrix(int source_size, int target_size) {
		dist = new double[source_size][target_size];
		prov = new Object[source_size][target_size];
	}
	
	private void setDistance(int n1, int n2, double d, Object provenance) {
		dist[n1][n2] = d;
		prov[n1][n2] = provenance;
	}

	private void addDistance(int n1, int n2, double d, Object provenance) {
		dist[n1][n2] += d;
		prov[n1][n2] = prov[n1][n2] + ":" + provenance;
	}

	
	public void setDistance(boolean reversed, int n1, int n2, double d, Object o) {
		if (reversed)
			setDistance(n2, n1, d, o);
		else
			setDistance(n1, n2, d, o);
	}

	public void addDistance(boolean reversed, int n1, int n2, double d, Object o) {
		if (reversed)
			addDistance(n2, n1, d, o);
		else
			addDistance(n1, n2, d, o);
	}

	
	protected double getDistance(int n1, int n2) {
		return dist[n1][n2];
	}

	public double getDistance(boolean reversed, int n1, int n2) {
		return reversed ? getDistance(n2, n1) : getDistance(n1, n2);
	}

	private String getProvenance(int n1, int n2) {
		return prov[n1][n2] == null ? NO_PROVENANCE : prov[n1][n2].toString();
	}
	
	public String getProvenance(boolean reversed, int n1, int n2) {
		return reversed ? getProvenance(n2, n1) : getProvenance(n1, n2);
	}

}
