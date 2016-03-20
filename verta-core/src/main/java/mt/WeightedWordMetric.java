package mt;

import java.util.Vector;

public class WeightedWordMetric extends Vector<FeatureMetric> {
	
	private static final long serialVersionUID = 1L;
	private double weight;
	
	public WeightedWordMetric(double weight) {
		super();
		this.weight=weight;
		
	}

	public double getWeight() {
		return weight;
	}
	
	
}
