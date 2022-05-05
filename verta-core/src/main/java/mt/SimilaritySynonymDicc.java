package mt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;

import mt.core.BaseSimilarity;
import mt.core.Similarity;
import mt.nlp.Word;

/**
 * Straight forward implementation of a in memory synonym dictionary using a
 * hashset
 *
 */
public class SimilaritySynonymDicc extends BaseSimilarity implements Similarity {

	double defaultValue = 0;
	HashSet<String> dicc;
	static char JOINWORDS = '#';

	public SimilaritySynonymDicc(String filename) {

		dicc = new HashSet<String>();

		try (BufferedReader in = new BufferedReader(new FileReader(filename))) {

			String buff;
			while ((buff = in.readLine()) != null) {
				String fields[] = buff.split("\t");
				try {
					dicc.add(fields[0] + JOINWORDS + fields[1]);
					dicc.add(fields[1] + JOINWORDS + fields[0]);
				} catch (Exception e) {
					System.err.println("Error reading dicc file:" + filename);
					System.err.println("AT LINE:" + buff);
					e.printStackTrace();
					System.exit(-1);
				}

			}
		} catch (Exception e) {
			System.err.println("Error opening dicc file:" + filename);
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public double similarity(String[] featureNames, Word proposedWord, Word referenceWord) {
		boolean hold = true;
		int i = 0;

		while (i < featureNames.length && hold) {
			String featureProposed = proposedWord.getFeature(featureNames[i]);
			String featureReference = referenceWord.getFeature(featureNames[i]);
			hold = (featureProposed.equals(featureReference)
					|| dicc.contains(featureProposed + JOINWORDS + featureReference));
			++i;
		}
		return hold ? MAXVAL : MINVAL;
	}

	public String getClassName() {
		return SimilaritySynonymDicc.class.getName();
	}

	public void setReversed(boolean reversed) {
	}
}
