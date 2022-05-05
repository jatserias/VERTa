package mt;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

class NgramMatchProTest {

	@Test
	void extra_word_test() {
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
