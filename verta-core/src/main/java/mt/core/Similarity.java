package mt.core;

import mt.nlp.Word;

/**
 * 
 * interface to an asymmetric similarity function
 * 
 * CONTRACT:
 * 
 * return [0..1] sim(x,x)=1
 * 
 *
 */
public interface Similarity {
	/// max value for a similarity function
	static double MAXVAL = 1.0;
	/// min value for a similarity function
	static double MINVAL = 0.0;

	/// calculate the similarity between proosedWord and referenceWord base on
	/// feature (featureName)
	public double similarity(String[] featureNames, Word proposedWord, Word referenceWord);

	public String getClassName();

	public void setReversed(boolean reversed);

	public void setWeight(double w);

	public double getWeight();
}
