package mt.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import edu.smu.tspell.wordnet.SynsetType;
import mt.SimilarityEqual;
import mt.WordMetric;
import mt.nlp.Sentence;
import mt.nlp.Word;
import mt.nlp.io.CONLLformat;
import mt.nlp.io.ReaderCONLL;
import verta.wn.ISynset;
import verta.wn.IWordNet;
import mt.nlp.Triples;

class VertaTest {

	@Test
	void test_word_similarity() {
		IWordNet wordnetI = Mockito.mock(IWordNet.class);

		Verta verta = new Verta("metric definition",
				new BufferedReader(
						new StringReader("GROUP\tLEX\t1\t10\tmt.WordMetric\n" + "1\tWORD\t100\tmt.SimilarityEqual\n")),
				wordnetI);
		Sentence referenceSentence = new Sentence();
		Sentence proposedSentence = new Sentence();

// single same word Sentence using only word metric
		referenceSentence.add(new Word("1", "1"));
		MetricResult result = verta.similarity(referenceSentence, referenceSentence);
		assertEquals(Similarity.MAX_VAL, result.getPrec(), 0.0000001, "Empty sentences have rec 0");
		assertEquals(Similarity.MAX_VAL, result.getRec(), 0.0000001, "Empty sentences have prec 0");

// different single word Sentence using only word metric
		referenceSentence.add(new Word("1", "1"));
		proposedSentence.add(new Word("1", "2"));
		result = verta.similarity(referenceSentence, proposedSentence);
		assertEquals(Similarity.MIN_VAL, result.getPrec(), 0.0000001, "Empty sentences have rec 0");
		assertEquals(Similarity.MIN_VAL, result.getRec(), 0.0000001, "Empty sentences have prec 0");

	}

	@Test
	void test_ngram_sentence_similarity_happypath() {
		IWordNet wordnetI = Mockito.mock(IWordNet.class);

		Verta verta = new Verta("metric definition",
				new BufferedReader(
						new StringReader("GROUP\tLEX\t1\t0\tmt.WordMetric\n" + "1\tWORD\t100\tmt.SimilarityEqual\n"
								+ "FGROUP\n" + "GROUP\tNGRAM\t2\t10\tmt.NgramMatch\t-1\n")),
				wordnetI);
		Sentence referenceSentence = new Sentence();
		Sentence proposedSentence = new Sentence();

		referenceSentence.add(new Word("1", "w1"));
		referenceSentence.add(new Word("2", "w2"));
		referenceSentence.add(new Word("3", "w3"));

		proposedSentence.add(new Word("1", "w3"));
		proposedSentence.add(new Word("2", "w1"));
		proposedSentence.add(new Word("3", "w2"));

		MetricResult result = verta.similarity(referenceSentence, proposedSentence);

		assertEquals(1.0 / 3.0, result.getPrec(), 0.0000001, "ngram sim 3 unigram of 3, 1 bigram of 2 ");
		assertEquals(1.0 / 3.0, result.getRec(), 0.0000001, "ngram sim 3 unigram of 3, 1 bigram of 2 ");

	}

	@Test
	void test_triples_sentence_similarity_happypath() {
		IWordNet wordnetI = Mockito.mock(IWordNet.class);

		Verta verta = new Verta("metric definition",
				new BufferedReader(new StringReader("GROUP	LEX	1	0	mt.WordMetric\n"
						+ "1	WORD	100	mt.SimilarityEqual\n" + "FGROUP\n"
						+ "GROUP\tDEP\t2\t100\tmt.SentenceSimilarityTripleOverlapping\tjar:/fluency_en/triplesmatch.conf\n")),
				wordnetI);

		Sentence referenceSentence = new Sentence();
		Sentence proposedSentence = new Sentence();
		/**
		 * source: [ <w1, nsubj, w2>, <w2, _, 0>, <w3, obj w2>] target: [ <w3, nsubj,
		 * w2>, <w1, obj, w2>, <w2, _, 0>]
		 * 
		 * align (lex) s-t ot t-s: w1-w1, w2-w2, w3-w3
		 * 
		 * align triples s-t: (ts1 , tt1, "0XX"), (ts2 , tt3, "XXX), (ts3 , tt2, "0XX"),
		 * align triples t-s: (ts1 , tt1, "0XX"), (ts2 , tt3, "0XX), (ts3 , tt2, "XXX"),
		 */
		referenceSentence.add(new Word("1", "w1").setFeature(Triples.ID_NAME, "1").setFeature("DEPLABEL", "nsubj")
				.setFeature("DEPHEAD", "2"));
		referenceSentence.add(new Word("2", "w2").setFeature(Triples.ID_NAME, "2").setFeature("DEPLABEL", "_")
				.setFeature("DEPHEAD", "0"));
		referenceSentence.add(new Word("3", "w3").setFeature(Triples.ID_NAME, "3").setFeature("DEPLABEL", "obj")
				.setFeature("DEPHEAD", "2"));

		proposedSentence.add(new Word("1", "w3").setFeature(Triples.ID_NAME, "1").setFeature("DEPLABEL", "nsubj")
				.setFeature("DEPHEAD", "3"));
		proposedSentence.add(new Word("2", "w1").setFeature(Triples.ID_NAME, "2").setFeature("DEPLABEL", "obj")
				.setFeature("DEPHEAD", "3"));
		proposedSentence.add(new Word("3", "w2").setFeature(Triples.ID_NAME, "3").setFeature("DEPLABEL", "_")
				.setFeature("DEPHEAD", "0"));

		MetricResult result = verta.similarity(referenceSentence, proposedSentence);

		assertEquals(1.0, result.getPrec(), 0.0000001, "pre when deps are equals once aligned");
		assertEquals(1.0, result.getRec(), 0.0000001, "recall when deps are equal once aligned");

	}

	@Test
	void test_triples_sentence_similarity_target_longer_sentence() {
		IWordNet wordnetI = Mockito.mock(IWordNet.class);

		Verta verta = new Verta("metric definition",
				new BufferedReader(new StringReader("GROUP\tLEX\t1\t0\tmt.WordMetric\n"
						+ "1\tWORD\t100	mt.SimilarityEqual\n" + "FGROUP\n"
						+ "GROUP\tDEP\t2\t100\tmt.SentenceSimilarityTripleOverlapping\tjar:/fluency_en/triplesmatch.conf\n")),
				wordnetI);

		Sentence referenceSentence = new Sentence();
		Sentence proposedSentence = new Sentence();

		referenceSentence.add(new Word("1", "w1").setFeature(Triples.ID_NAME, "1").setFeature("DEPLABEL", "nsubj")
				.setFeature("DEPHEAD", "2"));
		referenceSentence.add(new Word("2", "w2").setFeature(Triples.ID_NAME, "2").setFeature("DEPLABEL", "_")
				.setFeature("DEPHEAD", "0"));
		referenceSentence.add(new Word("3", "w3").setFeature(Triples.ID_NAME, "3").setFeature("DEPLABEL", "obj")
				.setFeature("DEPHEAD", "2"));

		proposedSentence.add(new Word("1", "w3").setFeature(Triples.ID_NAME, "1").setFeature("DEPLABEL", "nsubj")
				.setFeature("DEPHEAD", "3"));
		proposedSentence.add(new Word("2", "w1").setFeature(Triples.ID_NAME, "2").setFeature("DEPLABEL", "obj")
				.setFeature("DEPHEAD", "4"));
		proposedSentence.add(new Word("3", "w2").setFeature(Triples.ID_NAME, "3").setFeature("DEPLABEL", "_")
				.setFeature("DEPHEAD", "0"));
		proposedSentence.add(new Word("4", "w4").setFeature(Triples.ID_NAME, "4").setFeature("DEPLABEL", "_")
				.setFeature("DEPHEAD", "0"));

		MetricResult result = verta.similarity(referenceSentence, proposedSentence);
		assertEquals(0.5, result.getPrec(), 0.00000001, "Prec");
		assertEquals(0.666666666666666, result.getRec(), 0.00000001, "Recall");
	}

	@Test
	void test_triples_sentence_similarity_source_longer_sentence() {
		IWordNet wordnetI = Mockito.mock(IWordNet.class);

		Verta verta = new Verta("metric definition",
				new BufferedReader(new StringReader("GROUP	LEX	1	1	mt.WordMetric\n"
						+ "1	WORD	100	mt.SimilarityEqual\n" + "FGROUP"
						+ "GROUP\tDEP\t2\t100\tmt.SentenceSimilarityTripleOverlapping\tjar:/fluency_en/triplesmatch.conf\n")),
				wordnetI);
		
		Sentence referenceSentence = new Sentence();
		Sentence proposedSentence = new Sentence();

		referenceSentence.add(new Word("1", "w1").setFeature(Triples.ID_NAME, "1").setFeature("DEPLABEL", "nsubj")
				.setFeature("DEPHEAD", "2"));
		referenceSentence.add(new Word("2", "w2").setFeature(Triples.ID_NAME, "2").setFeature("DEPLABEL", "_")
				.setFeature("DEPHEAD", "0"));
		referenceSentence.add(new Word("3", "w3").setFeature(Triples.ID_NAME, "3").setFeature("DEPLABEL", "obj")
				.setFeature("DEPHEAD", "4"));
		referenceSentence.add(new Word("4", "w4").setFeature(Triples.ID_NAME, "3").setFeature("DEPLABEL", "_")
				.setFeature("DEPHEAD", "0"));

		proposedSentence.add(new Word("1", "w3").setFeature(Triples.ID_NAME, "1").setFeature("DEPLABEL", "nsubj")
				.setFeature("DEPHEAD", "3"));
		proposedSentence.add(new Word("2", "w1").setFeature(Triples.ID_NAME, "2").setFeature("DEPLABEL", "obj")
				.setFeature("DEPHEAD", "3"));
		proposedSentence.add(new Word("3", "w2").setFeature(Triples.ID_NAME, "3").setFeature("DEPLABEL", "_")
				.setFeature("DEPHEAD", "0"));

		MetricResult result = verta.similarity(referenceSentence, proposedSentence);
	
		assertEquals(1.0, result.getPrec(), 0.00000001, "Prec");
		assertEquals(0.75, result.getRec(), 0.00000001, "Recall");

	}

	String sentence_proposed_ana = "1 El       el       DA0MS0  DA  pos=determiner|type=article|gen=masculine|num=singular                 -     - - 2 spec     - -\n"
			+ "2 perro    perro    NCMS000 NC  pos=noun|type=common|gen=masculine|num=singular                        -     - - 6 suj      - -\n"
			+ "3 de       de       SP      SP  pos=adposition|type=preposition                                        -     - - 2 sp       - -\n"
			+ "4 el       el       DA0MS0  DA  pos=determiner|type=article|gen=masculine|num=singular                 -     - - 5 spec     - -\n"
			+ "5 Sr._Jose sr._jose NP00G00 NP  pos=noun|type=proper|neclass=location                                  B-LOC - - 3 sn       - -\n"
			+ "6 come     comer    VMIP3S0 VMI pos=verb|type=main|mood=indicative|tense=present|person=3|num=singular -     - - 0 sentence - -\n"
			+ "7 una      uno      DI0FS0  DI  pos=determiner|type=indefinite|gen=feminine|num=singular               -     - - 8 spec     - -\n"
			+ "8 manzana  manzana  NCFS000 NC  pos=noun|type=common|gen=feminine|num=singular                         -     - - 6 cd       - -\n"
			+ "";

	String sentence_reference_ana = "1 Los                     el                      DA0MP0  DA pos=determiner|type=article|gen=masculine|num=plural   -      - - 2 spec     - -\n"
			+ "2 perros                  perro                   NCMP000 NC pos=noun|type=common|gen=masculine|num=plural          -      - - 0 sentence - -\n"
			+ "3 de                      de                      SP      SP pos=adposition|type=preposition                        -      - - 2 sp       - -\n"
			+ "4 el                      el                      DA0MS0  DA pos=determiner|type=article|gen=masculine|num=singular -      - - 5 spec     - -\n"
			+ "5 Sr._Pepe_comen_manzanas sr._pepe_comen_manzanas NP00V00 NP pos=noun|type=proper|neclass=other                     B-MISC - - 3 sn       - -\n"
			+ "";

	@Test
	void test_demo_example() throws Exception {
		IWordNet wordnetI = Mockito.mock(IWordNet.class);
		ISynset[] no_synset = new ISynset[0];
		when(wordnetI.getSynsets(org.mockito.ArgumentMatchers.anyString())).thenReturn(no_synset);
		when(wordnetI.getSynsets(org.mockito.ArgumentMatchers.anyString(),
				org.mockito.ArgumentMatchers.any(SynsetType.class))).thenReturn(no_synset);

		// /conf/triplesmatch_spa_adeq.conf
		Verta verta = new Verta("jar:conf/jabmetric_spa_adeq_new.conf", new BufferedReader(
				new InputStreamReader(this.getClass().getResourceAsStream("/conf/jabmetric_spa_adeq_new.conf"))),
				wordnetI);

		CONLLformat fmt = new CONLLformat("jar:/conf/conllFreeling.fmt");

		BufferedReader hyp = new BufferedReader(new StringReader(sentence_proposed_ana));
		Sentence proposedSentence = ReaderCONLL.read(hyp, fmt);

		BufferedReader ref = new BufferedReader(new StringReader(sentence_reference_ana));
		Sentence referenceSentence = ReaderCONLL.read(ref, fmt);

		MetricResult result = verta.similarity(referenceSentence, proposedSentence);
		
		assertEquals(0.48714285714285716, result.getPrec(), 0.00000001, "Prec");
		assertEquals(0.7909999999999999, result.getRec(), 0.00000001, "Recall");
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

		final Sentence proposedSentence1 = proposedSentence;
		final Sentence referenceSentence = proposedSentence;
		double[] res = new double[2];
		AlignmentBuilder builder = new AlignmentBuilderBestMatch();

		// calculate all distances
		DistanceMatrix dist1 = Verta.create_word_distance_matrix(wm, proposedSentence1, referenceSentence);
		AlignmentImplSingle align = new AlignmentImplSingle(proposedSentence1.size(), referenceSentence.size());
		// TODO configure alignment strategy
		builder.build(align, dist1);
		double prec = Verta.calculate_similarity_for_alignment(dist1, align, proposedSentence1, false);

		DistanceMatrix dist_rev = Verta.create_word_distance_matrix(wm, referenceSentence, proposedSentence1);
		AlignmentImplSingle align_rev = align = new AlignmentImplSingle(proposedSentence1.size(),
				referenceSentence.size());
		builder.build(align_rev, dist_rev);
		double rec = Verta.calculate_similarity_for_alignment(dist_rev, align_rev, referenceSentence, false);

		res[0] = prec;
		res[1] = rec;

		double[] similarity = res;

		assertEquals(Similarity.MAX_VAL, similarity[0], 0.00000001, "prec sim(A,A)=1");
		assertEquals(Similarity.MAX_VAL, similarity[0], 0.00000001, "rec  sim(A,A)=1");

	}
}
