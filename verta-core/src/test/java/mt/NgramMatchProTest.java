package mt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import mt.core.DistanceMatrix;
import mt.core.MetricActivationCounter;
import mt.core.NgramMatchPro;
import mt.core.SentenceAlignment;
import mt.core.SimilarityResult;
import mt.nlp.Sentence;
import mt.nlp.Word;
import mt.nlp.Ngram;

class NgramMatchProTest {

	@Test 
	void test_aligned_ngram_with_offset() {
		Ngram source_ngram = new Ngram(0, 2);
		Ngram target_ngram = new Ngram(3, 2);
		double [][] t_dist= {
				{0, 0, 0, 1, 0}, 
				{0 ,0, 0 ,0, 1}, 
			};
		final SentenceAlignment dist = new DistanceMatrix(t_dist);
		assertTrue(mt.NgramMatch.compareNgramSameSize(source_ngram, target_ngram, dist), "ngram different position aligned");
	}

	@Test 
	void test_aligned_ngram_with_offset_not_start() {
		Ngram source_ngram = new Ngram(2, 2);
		Ngram target_ngram = new Ngram(3, 2);
		double [][] t_dist= {
				{1, 0, 0, 0, 0}, 
				{0 ,1, 0 ,0, 0}, 
				{0, 0, 0, 1, 0}, 
				{0 ,0, 0 ,0, 1}, 
			};
		final SentenceAlignment dist = new DistanceMatrix(t_dist);
		assertTrue(mt.NgramMatch.compareNgramSameSize(source_ngram, target_ngram, dist), "ngram different position aligned");
	}
	
	@Test
	void test_totalngrams() {
		assertEquals(3, NgramMatchPro.totalngrams(2, 4),"bigrams for size 4");
		assertEquals(4, NgramMatchPro.totalngrams(2, 5),"bigrams for size 5");
	}
	
	@Test
	void test_compareNgrams_from_sentences() {
		final Sentence s1 = new Sentence();
		s1.add(new Word("1", "1"));
		s1.add(new Word("2", "2"));
		s1.add(new Word("3", "3"));
		s1.add(new Word("4", "4"));
		
		final Sentence s2 = new Sentence();
		s2.add(new Word("1", "1"));
		s2.add(new Word("2", "2"));
		s2.add(new Word("4", "4"));
		s2.add(new Word("3", "3"));
		s2.add(new Word("5", "5"));
		
		double [][] t_dist= {
				{1, 0, 0, 0, 0}, 
				{0 ,1, 0 ,0, 0}, 
				{0, 0, 0, 1, 0}, 
				{0 ,0, 0 ,0, 1}, 
			};
		
		final SentenceAlignment dist = new DistanceMatrix(t_dist);
		
		assertEquals(2.0, NgramMatch.compareNgrams(2, s1, s2, dist, System.err), 0.0000001, "ngrams mid extra word");
	}
	
	@Test
	void test_extra_word_end() {
		MetricActivationCounter counters = new MetricActivationCounter();
		NgramMatchPro matcher = new NgramMatchPro(counters, "2", "3");
		final Sentence s1 = new Sentence();
		s1.add(new Word("1", "1"));
		s1.add(new Word("2", "2"));
		s1.add(new Word("3", "3"));
		s1.add(new Word("4", "4"));
		final Sentence s2 = new Sentence();
		s2.add(new Word("1", "1"));
		s2.add(new Word("2", "2"));
		s2.add(new Word("4", "4"));
		s2.add(new Word("3", "3"));
		s2.add(new Word("5", "5"));
		double [][] t_dist= {
								{1, 0, 0, 0, 0}, 
								{0 ,1, 0 ,0, 0}, 
								{0, 0, 0, 1, 0}, 
								{0, 0, 0, 0, 0}
							};
		final SentenceAlignment dist = new DistanceMatrix(t_dist);
		
		SimilarityResult result = matcher.similarity(s1, s2, dist, System.err);
		assertEquals(0.166666666666, result.getPrec(), 0.0000001, "size 4, 1 ngram-2, 4 ngram-1");
		assertEquals(0.125, result.getRec(), 0.0000001, "size 5, 1 ngram-2, 4 ngram-1");
	}
	
	@Test
	void test_extra_words_midle() {
		MetricActivationCounter counters = new MetricActivationCounter();
		NgramMatchPro matcher = new NgramMatchPro(counters, "2", "2");
		final Sentence s1 = new Sentence();
		s1.add(new Word("1", "1"));
		s1.add(new Word("2", "2"));
		s1.add(new Word("3", "3"));
		s1.add(new Word("4", "4"));
		
		final Sentence s2 = new Sentence();
		s2.add(new Word("1", "1"));
		s2.add(new Word("2", "2"));
		s2.add(new Word("3", "N"));
		s2.add(new Word("4", "N"));
		s2.add(new Word("5", "3"));
		s2.add(new Word("6", "4"));
		
		double [][] t_dist= {
								{1, 0, 0, 0, 0, 0}, 
								{0 ,1, 0 ,0, 0, 0}, 
								{0, 0, 0, 0, 1, 0}, 
								{0, 0, 0, 0, 0, 1}
							};
		final SentenceAlignment dist = new DistanceMatrix(t_dist);

		SimilarityResult result = matcher.similarity(s1, s2, dist, System.err);
		assertEquals(2.0 / 3.0, result.getPrec(), 0.0000001, "2 / 3 ngrams");
		assertEquals(2.0 / 5.0, result.getRec(), 0.0000001, " 2 / 5 ngrams");
	}

	private static Stream<Arguments> generator() {		 
		 return Stream.of(
		   Arguments.of(1, 1, 1, "seq 1 ngram 1"),  
		   Arguments.of(2, 1, 2, "seq 2 ngram 1"),
		   Arguments.of(2, 2, 1, "seq 2 ngram 2"),
		   Arguments.of(3, 1, 3, "seq 3 ngram 1"),
		   Arguments.of(3, 2, 2, "seq 3 ngram 2"),
		   Arguments.of(4, 2, 3, "seq 4 ngram 2"),
		   Arguments.of(4, 3, 2, "seq 4 ngram 3"),
		   Arguments.of(4, 4, 1, "seq 4 ngram 4")
		  );
		}
	@ParameterizedTest
	@MethodSource("generator")
	void test_number_of_ngrams(int sentence_size, int ngram_size, int expected_result, String test_id) {
		 double totalngrams = NgramMatchPro.totalngrams(ngram_size, sentence_size);
		 assertEquals(expected_result, totalngrams, 0.0000001, test_id);
	}
}
