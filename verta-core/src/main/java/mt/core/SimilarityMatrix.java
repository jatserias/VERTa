package mt.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SimilarityMatrix {

	protected static final String NO_PROVENANCE = "NULL";

	/// distance from source to target words
	private double[][] dist;
	private Object[][] prov;
	
	
	public SimilarityMatrix(double[][] m) {
		dist = m;
		prov = new Object[m.length][m[0].length];
	}
	
	public SimilarityMatrix(int source_size, int target_size) {
		dist = new double[source_size][target_size];
		prov = new Object[source_size][target_size];
	}
	
	@JsonIgnore
	public void setDistance(int n1, int n2, double d, Object provenance) {
		dist[n1][n2] = d;
		prov[n1][n2] = provenance;
	}

	@JsonIgnore
	public void addDistance(int n1, int n2, double d, Object provenance) {
		dist[n1][n2] += d;
		prov[n1][n2] = prov[n1][n2] + ":" + provenance;
	}
	
	@JsonIgnore
	public double getDistance(int n1, int n2) {
		return dist[n1][n2];
	}

	@JsonIgnore
	public String getProvenance(int n1, int n2) {
		return prov[n1][n2] == null ? NO_PROVENANCE : prov[n1][n2].toString();
	}
	
}
