package mt.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import mt.nlp.Sentence;
import mt.nlp.Word;
import verta.wn.WordNetAPI;



class VertaTest {

	@Test
	void test_word_similarity() {
		WordNetAPI wordnetApi = Mockito.mock(WordNetAPI.class);
		
		Verta verta = new Verta("metric definition", new BufferedReader(new StringReader("GROUP	LEX	1	10	mt.WordMetric\n"
				+ "1	WORD	100	mt.SimilarityEqual\n"
				+ "FGROUP"
				)), wordnetApi);
		Sentence referenceSentence = new Sentence();
		Sentence proposedSentence = new Sentence();

// @TODO check case with empty Sentence
//
//		MetricResult result = verta.similarity(referenceSentence, proposedSentence, false, null);
//		assertEquals("Empty sentences have rec 0", Similarity.MINVAL, result.getPrec(), 0.0000001);
//		assertEquals("Empty sentences have prec 0", Similarity.MINVAL, result.getRec(), 0.0000001);
//	
		
// single same word Sentence using only word metric
		referenceSentence.add(new Word("1", "1"));
		MetricResult result = verta.similarity(referenceSentence, referenceSentence);
		assertEquals(Similarity.MAXVAL, result.getPrec(), 0.0000001, "Empty sentences have rec 0");
		assertEquals(Similarity.MAXVAL, result.getRec(), 0.0000001, "Empty sentences have prec 0");
		
// different single word Sentence using only word metric
		referenceSentence.add(new Word("1", "1"));
		proposedSentence.add(new Word("1", "2"));
		result = verta.similarity(referenceSentence, proposedSentence);
		assertEquals(Similarity.MINVAL, result.getPrec(), 0.0000001,"Empty sentences have rec 0");
		assertEquals(Similarity.MINVAL, result.getRec(), 0.0000001, "Empty sentences have prec 0");
		
		
	}
	
	@Test
	void test_sentence_similarity() {
		WordNetAPI wordnetApi = Mockito.mock(WordNetAPI.class);
		
		Verta verta = new Verta("metric definition", new BufferedReader(new StringReader(
				"GROUP	LEX	1	0	mt.WordMetric\n"
						+ "1	WORD	100	mt.SimilarityEqual\n"
						+ "FGROUP\n"
						+ "GROUP	NGRAM	2	10	mt.NgramMatch	-1\n"
						+ "FGROUP"
				)), wordnetApi);
		Sentence referenceSentence = new Sentence();
		Sentence proposedSentence = new Sentence();
		
		referenceSentence.add(new Word("1", "w1"));
		referenceSentence.add(new Word("2", "w2"));
		referenceSentence.add(new Word("3", "w3"));
		
		proposedSentence.add(new Word("1", "w3"));
		proposedSentence.add(new Word("2", "w1"));
		proposedSentence.add(new Word("3", "w2"));
		
		MetricResult result = verta.similarity(referenceSentence, proposedSentence);
		result.dump(System.err);
		assertEquals(1.0/3.0, result.getPrec(), 0.0000001, "ngram sim 3 unigram of 3, 1 bigram of 2 ");
		assertEquals(1.0/3.0, result.getRec(), 0.0000001, "ngram sim 3 unigram of 3, 1 bigram of 2 ");
		
	}

}
