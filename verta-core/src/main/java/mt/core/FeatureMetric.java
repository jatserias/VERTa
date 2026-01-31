package mt.core;

import java.io.PrintStream;
import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mt.nlp.Word;
import verta.wn.IWordNet;

/// Similarity function over a list of attributes and a weight
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FeatureMetric {

	/// names of the features
	private String[] featureNames;
	/// similarity function
	private IFeaturesWordSimilarity similarityFunction;
	/// feature Weight
	private double weight;
	/// reversed function
	private boolean reversed;

	public FeatureMetric(String featureName, IFeaturesWordSimilarity similarityFunction, double weight) {
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

	public String toString() {
		return this.getClass().getCanonicalName()+"."+similarityFunction.toString();
	}

	public void compile(IWordNet wordNet) {
		similarityFunction.compile(wordNet);
	}
}
