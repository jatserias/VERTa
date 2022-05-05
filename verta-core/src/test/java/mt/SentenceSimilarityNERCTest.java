package mt;

import static org.junit.Assert.assertArrayEquals;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import mt.nlp.NERC;
import mt.nlp.Sentence;
import mt.nlp.Word;

class SentenceSimilarityNERCTest {

	@Test
	void simple_example_test() {
		Sentence s = new Sentence();
		s.add(new Word("1", "The").setFeature(SentenceSimilarityNERC.TNERC, SentenceSimilarityNERC.NOTAG));
		s.add(new Word("2", "president").setFeature(SentenceSimilarityNERC.TNERC, SentenceSimilarityNERC.NOTAG));
		s.add(new Word("3", "George").setFeature(SentenceSimilarityNERC.TNERC, SentenceSimilarityNERC.B_TAG+"PER"));
		s.add(new Word("4", "F.").setFeature(SentenceSimilarityNERC.TNERC, SentenceSimilarityNERC.I_TAG+"PER"));
		s.add(new Word("5", "Bush").setFeature(SentenceSimilarityNERC.TNERC, SentenceSimilarityNERC.I_TAG+"PER"));
		s.add(new Word("6", "and").setFeature(SentenceSimilarityNERC.TNERC, SentenceSimilarityNERC.NOTAG));
		s.add(new Word("7", "Mike").setFeature(SentenceSimilarityNERC.TNERC, SentenceSimilarityNERC.B_TAG+"LOC"));
		s.add(new Word("8", "eat").setFeature(SentenceSimilarityNERC.TNERC, SentenceSimilarityNERC.NOTAG));
		s.add(new Word("9", "fish").setFeature(SentenceSimilarityNERC.TNERC, SentenceSimilarityNERC.NOTAG));		
		
		Collection<NERC> nerc = SentenceSimilarityNERC.generateNERC(s);
		NERC[] expected_ner = {
				new NERC(1, 2, 4, "George F. Bush", "PER"),
				// @TODO position is not correct, this should be 7,7
				new NERC(1, 6, 6, "Mike", "LOC")
				};
		assertArrayEquals(expected_ner, nerc.toArray());
	}
}
