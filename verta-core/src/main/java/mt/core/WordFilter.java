package mt.core;

import mt.nlp.Word;

/// A class to filter out words from the lexical similarity
public class WordFilter {

	/// Filter out punctuation
	public static boolean filter(final Word w) {
		char c = w.getFeature("POS").charAt(0);
		return ((c > 'Z') || (c < 'A'));
	}
}
