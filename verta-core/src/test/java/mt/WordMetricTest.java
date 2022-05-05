package mt;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import mt.core.DistanceMatrix;
import mt.core.FeatureMetric;
import mt.core.Similarity;
import mt.core.WeightedWordMetric;
import mt.nlp.Sentence;
import mt.nlp.Word;

public class WordMetricTest {

	  
	private static Stream<Arguments> generator() {

		 return Stream.of(
		   Arguments.of(new Word("1", "a"), new Word("2", "a"), 1.0),
		   Arguments.of(new Word("1", "a"), new Word("2", "b"), 0.0));
		}
		
	@ParameterizedTest
	@MethodSource("generator")
	void test_similarity(final Word proposedWord, final Word targetWord, double expected_result) {

		WordMetric wm = simple_metric_helper();
		
		double actual_result = wm.similarity(proposedWord, targetWord);
		assertEquals(expected_result, actual_result, 0.0001, "Word similarity");
		
	}

	private WordMetric simple_metric_helper() {
		// basic similarity  metric using one group, weight 100 with one feature WORD and using equality
		WordMetric wm = new WordMetric();
		WeightedWordMetric group = new WeightedWordMetric(1.0);
		Similarity sm = new SimilarityEqual();
		group.add(new FeatureMetric("WORD", sm, 100));
		wm.featureMetrics.put("1", group);
		return wm;
	}


	@Test
	void test_bestMatch() {
		// not used as input
		Integer align[] = {1};
		boolean[] taken = {false};
		double[][] distance_matrix = {{0.1, 0.3}};
		DistanceMatrix dist = new DistanceMatrix(distance_matrix);
		boolean reversed = false;

		int sw = 0;

		Word w = new Word("1", "hola");

	
		Sentence targetSentence = new Sentence();
		targetSentence.add(w);
	
		WordMetric wm = simple_metric_helper();
		assertEquals(1.0, wm.bestMatch(reversed, sw, w, targetSentence, align, taken, dist), 0.0001, "Best match score");
		assertTrue(taken[0], "Word is taken (side effect)");
		assertEquals(align[0], new Integer(0), "Align is update (side effect)");
		assertEquals(dist.getDistance(reversed,0 ,0), 1.0, 0.0001, "Distance matrix is updated (side effect)");
		
	}

	@Test
	void test_sentenceSimilarity() {

		Integer align[] = {0};
		boolean[] taken = {false};
		double[][] distance_matrix = {{0.0, 0.0}};
		DistanceMatrix dist = new DistanceMatrix(distance_matrix);
		boolean reversed = false;
			
		Word w = new Word("1", "hola");

		Sentence proposedSentence = new Sentence();
		proposedSentence.add(w);
		
		Sentence targetSentence = new Sentence();
		targetSentence.add(w);
		
		WordMetric wm = simple_metric_helper();
		assertEquals(1.0, wm.sentenceSimilarity(align, taken, dist, reversed,  proposedSentence, targetSentence), 0.0001, "test") ;
	}
	
	
	@Test
	void test_similarity_same_sentence_is_1() {
		WordMetric wm = simple_metric_helper();
		Sentence proposedSentence = new Sentence();
		proposedSentence.add(new Word("1", "1"));
		proposedSentence.add(new Word("2", "2"));
	
		double [][] d_dist = { {Similarity.MAXVAL, Similarity.MAXVAL}, {Similarity.MAXVAL, Similarity.MAXVAL}};
		DistanceMatrix dist = new DistanceMatrix(d_dist);
		
		
		double[] similarity = wm.similarity(proposedSentence, proposedSentence , dist, null);
		
		assertEquals(similarity[0], Similarity.MAXVAL, 0.00000001, "prec sim(A,A)=1");
		assertEquals(similarity[0], Similarity.MAXVAL, 0.00000001, "rec  sim(A,A)=1");
		
	}
	
}

