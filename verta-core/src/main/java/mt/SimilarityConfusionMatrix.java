package mt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import mt.core.BaseSimilarity;
import mt.core.Similarity;
import mt.nlp.Word;

///similarity using a confusion matrix
@Slf4j
public class SimilarityConfusionMatrix extends BaseSimilarity implements Similarity {
	private static final String JOIN_STRING = "+";
	double defaultValue;
	HashMap<String, Double> confusionMatrix;
	double maxWeight = 0;

	public SimilarityConfusionMatrix(String filename) {
		defaultValue = 0;
		confusionMatrix = new HashMap<>();

		// read confusion matrix from filename
		try (BufferedReader in = new BufferedReader(new FileReader(filename))) {

			String buff;
			while ((buff = in.readLine()) != null) {
				String[] fields = buff.split("\t");
				try {
					double rWeight = Double.parseDouble(fields[2]);
					if (rWeight > maxWeight)
						maxWeight = rWeight;

					confusionMatrix.put(fields[0] + JOIN_STRING + fields[1], rWeight);
				} catch (Exception e) {
					log.error("Error reading weight on confusionMatrix file:" + filename+" AT LINE:" + buff, e);
					System.exit(-1);
				}

			}
		} catch (Exception e) {
			System.err.println("Error opening file:" + filename);

			e.printStackTrace();
			System.exit(-1);
		}
	}

	// @Override
	public double similarity(String[] featureNames, Word proposedWord, Word referenceWord) {
		int i = 0;
		double w = 0;
		while (i < featureNames.length) {

			String featureName = featureNames[i];
			if (proposedWord.getFeature(featureName).compareTo(referenceWord.getFeature(featureName)) == 0)
				return 1;
			Double res = confusionMatrix
					.get(proposedWord.getFeature(featureName) + JOIN_STRING + referenceWord.getFeature(featureName));
			if (res == null) {
				System.err.println("NOT found:" + proposedWord.getFeature(featureName) + JOIN_STRING
						+ referenceWord.getFeature(featureName));
				w += defaultValue;
			} else {
				System.err.println("Found:" + proposedWord.getFeature(featureName) + JOIN_STRING
						+ referenceWord.getFeature(featureName) + " -> " + res);
				w += res / maxWeight;
			}
			++i;
		}
		// Normalize
		return w / featureNames.length;
	}

	public String getClassName() {
		return SimilarityConfusionMatrix.class.getName();
	}

	public void setReversed(boolean reversed) {
	}
}
