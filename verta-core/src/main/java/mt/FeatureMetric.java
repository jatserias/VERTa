package mt;

import java.io.PrintStream;
import java.util.Arrays;
/***
 * 
 *  funcio de similitud, sobre una llista d'atributs i un pes?
 * 
 * @author jordi
 *
 */
public class FeatureMetric {
 
	/// names of the features
	String[] featureNames;
	/// similarity function
	Similarity similarityFunction;
	
	/// feature Weight
	double weight;
	
	public boolean reversed;
	
	public FeatureMetric(String featureName, Similarity similarityFunction, double weight) {
		this.weight=weight;
		this.featureNames=featureName.split(",");
		this.similarityFunction=similarityFunction;
	}
	
	double similarity(Word proposedWord, Word targetWord) {
		similarityFunction.setReversed(reversed);
		return weight * similarityFunction.similarity(featureNames, proposedWord, targetWord);
	}

	public void dump(PrintStream trace) {
		trace.println("<fm name=\""+Arrays.deepToString(featureNames)+"\" weight=\""+weight+"\" class=\""+similarityFunction.getClass().getCanonicalName()+"\"/>");
	}
	
	public String getClassName() {
		return similarityFunction.getClassName();
	}
}
