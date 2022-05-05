package mt.core;

import java.io.PrintStream;
import java.util.Arrays;

import mt.nlp.Word;

/// Similarity function over a list of attributes and a weight
public class FeatureMetric {

	/// names of the features
	public String[] featureNames;
	/// similarity function
	Similarity similarityFunction;

	/// feature Weight
	double weight;

	public boolean reversed;

	public FeatureMetric(String featureName, Similarity similarityFunction, double weight) {
		this.weight = weight;
		this.featureNames = featureName.split(",");
		this.similarityFunction = similarityFunction;
	}

	public double similarity(Word proposedWord, Word targetWord) {
		similarityFunction.setReversed(reversed);
		return weight * similarityFunction.similarity(featureNames, proposedWord, targetWord);
	}

	public void dump(PrintStream trace) {
		trace.println("<fm name=\"" + Arrays.deepToString(featureNames) + "\" weight=\"" + weight + "\" class=\""
				+ similarityFunction.getClass().getCanonicalName() + "\"/>");
	}

	public String getClassName() {
		return similarityFunction.getClassName();
	}
}
