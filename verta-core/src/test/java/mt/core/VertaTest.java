package mt.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import mt.MTsimilarity;
import mt.SimilarityEqual;
import mt.WordMetric;
import mt.nlp.Sentence;
import mt.nlp.Word;
import verta.wn.WordNetAPI;
import verta.xml.AlignmentImplXMlDumper;
import mt.nlp.Triples;


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
	void test_ngram_sentence_similarity_happypath() {
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

	@Test
	void test_triples_sentence_similarity_happypath() {
		WordNetAPI wordnetApi = Mockito.mock(WordNetAPI.class);
		
		Verta verta = new Verta("metric definition", new BufferedReader(new StringReader(
				"GROUP	LEX	1	0	mt.WordMetric\n"
						+ "1	WORD	100	mt.SimilarityEqual\n"
						+ "FGROUP\n"
						+ "GROUP\tDEP\t2\t100\tmt.SentenceSimilarityTripleOverlapping\tjar:/fluency_en/triplesmatch.conf\n"
						+ "FGROUP"
				)), wordnetApi);
		
		Sentence referenceSentence = new Sentence();
		Sentence proposedSentence = new Sentence();
		
		referenceSentence.add(new Word("1", "w1").setFeature(Triples.ID_NAME, "1").setFeature(Triples.DEPLABEL_NAME, "nsubj").setFeature(Triples.DEPHEAD_NAME, "2"));
		referenceSentence.add(new Word("2", "w2").setFeature(Triples.ID_NAME, "2").setFeature(Triples.DEPLABEL_NAME, "_").setFeature(Triples.DEPHEAD_NAME, "0"));
		referenceSentence.add(new Word("3", "w3").setFeature(Triples.ID_NAME, "3").setFeature(Triples.DEPLABEL_NAME, "obj").setFeature(Triples.DEPHEAD_NAME, "2"));
		
		proposedSentence.add(new Word("1", "w3").setFeature(Triples.ID_NAME, "1").setFeature(Triples.DEPLABEL_NAME, "nsubj").setFeature(Triples.DEPHEAD_NAME, "3"));
		proposedSentence.add(new Word("2", "w1").setFeature(Triples.ID_NAME, "2").setFeature(Triples.DEPLABEL_NAME, "obj").setFeature(Triples.DEPHEAD_NAME, "3"));
		proposedSentence.add(new Word("3", "w2").setFeature(Triples.ID_NAME, "3").setFeature(Triples.DEPLABEL_NAME, "_").setFeature(Triples.DEPHEAD_NAME, "0"));
		
		MetricResult result = verta.similarity(referenceSentence, proposedSentence);
		result.dump(System.err);
		assertEquals(1.0, result.getPrec(), 0.0000001, "pre when deps are equals once aligned");
		assertEquals(1.0, result.getRec(), 0.0000001, "recall when deps are equal once aligned");
		
	}
	
	@Test
	void test_similarity_same_sentence_is_1() {
		
	    WordMetric wm = new WordMetric();
		WeightedWordMetric group = new WeightedWordMetric(1.0);
		Similarity sm = new SimilarityEqual();
		group.add(new FeatureMetric("WORD", sm, 100));
		wm.featureMetrics.put("1", group);
		Sentence proposedSentence = new Sentence();
		proposedSentence.add(new Word("1", "1"));
		proposedSentence.add(new Word("2", "2"));
					
		DistanceMatrix dist = new DistanceMatrix(proposedSentence, proposedSentence);
		final Sentence proposedSentence1 = proposedSentence;
		final Sentence referenceSentence = proposedSentence;
		double[] res = new double[2];
		AlignmentBuilder builder = new AlignmentBuilderBestMatch();
		
		// calculate all distances
		DistanceMatrix dist1 = Verta.create_word_distance_matrix(wm, false, proposedSentence1, referenceSentence);
		AlignmentImpl align = new AlignmentImpl(proposedSentence1.size(), referenceSentence.size());
		// TODO configure alignment strategy
		builder.build(false, align, dist1);
		double prec = Verta.calculate_similarity_for_alignment(dist1, align, false, proposedSentence1, false);
		
		
		DistanceMatrix dist_rev = Verta.create_word_distance_matrix(wm, true,  referenceSentence, proposedSentence1);
		AlignmentImpl align_rev = align = new AlignmentImpl(proposedSentence1.size(), referenceSentence.size());
		builder.build(true, align_rev, dist_rev);
		double rec = Verta.calculate_similarity_for_alignment(dist_rev, align_rev, true, referenceSentence, false);
		
		res[0] = prec;
		res[1] = rec;
		
		// dump the alignment
		if (MTsimilarity.DUMP) {
			AlignmentImplXMlDumper.dump(align, null);
		}
		
		
		double[] similarity = res;
		dist.dump(System.err);
		assertEquals(Similarity.MAXVAL, similarity[0], 0.00000001, "prec sim(A,A)=1");
		assertEquals(Similarity.MAXVAL, similarity[0], 0.00000001, "rec  sim(A,A)=1");
		
	}
}
