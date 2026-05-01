package mt.core;

public abstract class BaseSimilarity implements IFeaturesWordSimilarity {
	double weight=mt.core.IFeaturesWordSimilarity.MAX_VAL;
	public void setWeight(double w) {weight=w;}
	public double getWeight(){return weight;}
	public void compile(Object obj) {}
}
