package mt;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import mt.core.DistanceMatrix;
import mt.core.MetricActivationCounter;
import mt.core.ISentenceAlignment;
import mt.core.Similarity;
import mt.nlp.Sentence;
import mt.nlp.Triples;
import mt.nlp.Word;

class SentenceSimilarityTripleOverlappingTest {

	@Test
	void test_tripleGenerator_happypath() {
		Sentence s = new Sentence();
		s.add(new Word("1", "cat").setFeature(Triples.ID_NAME, "1").setFeature("DEPHEAD", "2").setFeature("DEPLABEL",
				"subj"));
		s.add(new Word("2", "eats").setFeature(Triples.ID_NAME, "2").setFeature("DEPHEAD", "_").setFeature("DEPLABEL",
				"_"));
		s.add(new Word("3", "fish").setFeature(Triples.ID_NAME, "3").setFeature("DEPHEAD", "2").setFeature("DEPLABEL",
				"obj"));

		// SentenceSimilarityTripleOverlapping.FILTER_TOP = true;
		Triples[] result = SentenceSimilarityTripleOverlapping.tripleGenerator(s, "DEPHEAD", "DEPLABEL")
				.toArray(new Triples[0]);
		Arrays.sort(result);

		Triples[] expected_result = { new Triples("_", -1, 2, "TOP", "eats"), new Triples("subj", 2, 1, "eats", "cats"),
				new Triples("obj", 2, 3, "eats", "fish") };
		Arrays.sort(expected_result);

		assertArrayEquals(expected_result, result, "Sentence triples");
	}

	@Test
	void test_createDist() throws IOException {
		List<Triples> ts1 = new ArrayList<Triples>();
		ts1.add(new Triples("a", 1, 2));
		List<Triples> ts2 = new ArrayList<Triples>();
		ts2.add(new Triples("a", 1, 2));
		double t_dist[][] = { { 0.5, 0.5, 0.5 }, { 0.5, 0.5, 0.5 } };
		ISentenceAlignment dist = new DistanceMatrix(t_dist);
		MetricActivationCounter counters = new MetricActivationCounter();
		SentenceSimilarityTripleOverlapping sim = new SentenceSimilarityTripleOverlapping(counters, "test",
				new BufferedReader(
						new StringReader("1\ttop	agent	att	dobj	iobj	obj-prep	pred	subj	subj-pac\n"
								+ "0.5	ok	adjmod	cc	co-%	dconj	dep	dep-%	sn-mod	sp-mod	sp-obj	subord-mod	vsubord	ador	aux\n"
								+ "0.5	last	dprep	dverb	es	espec	term\n" + "%%#PATTERNS\n"
								+ "## perfect: COMPLETE\n" + "(X,X,X) : 1.0\n" + "## Partial NOLABEL\n"
								+ "(O,X,X) : 1.0\n" + "## PARTIAL_NOMOD\n" + "(X,X,O) : 1.0\n" + "## partial NOHEAD\n"
								+ "#(X,O,X) : 0.7\n")),
				"DEPHEAD", "DEPLABEL");

		DistanceMatrix result = sim.createDist(ts1, ts2, dist);
		assertEquals(result.getDistance(0, 0), Similarity.MAX_VAL, "one perfect triple");
	}

}
