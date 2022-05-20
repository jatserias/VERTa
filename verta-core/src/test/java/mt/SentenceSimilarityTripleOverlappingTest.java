package mt;



import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import mt.core.AlignmentImpl;
import mt.core.DistanceMatrix;
import mt.core.MetricActivationCounter;
import mt.core.SentenceAlignment;
import mt.core.Similarity;
import mt.core.TripleMatchPattern;
import mt.nlp.Sentence;
import mt.nlp.Triples;
import mt.nlp.Word;

class SentenceSimilarityTripleOverlappingTest {

	@Disabled("untill we find why TOP dep is randomly behaving")
	@Test
	void test() {
		Sentence s = new Sentence();
		s.add(new Word("1", "cat").setFeature(Triples.ID_NAME, "1").setFeature("DEPHEAD", "2").setFeature("DEPLABEL", "subj"));
		s.add(new Word("2", "eats").setFeature(Triples.ID_NAME, "2").setFeature("DEPHEAD", "_").setFeature("DEPLABEL", "_"));
		s.add(new Word("3", "fish").setFeature(Triples.ID_NAME, "3").setFeature("DEPHEAD", "2").setFeature("DEPLABEL", "obj"));
		
		// SentenceSimilarityTripleOverlapping.FILTER_TOP = true;
		List<Triples> result = SentenceSimilarityTripleOverlapping.tripleGenerator(s);
		Triples[] expected_result = {
				new Triples("_", -1, 1, "TOP", "eats"),
				new Triples("subj", 2, 1, "eats", "cats"),
				new Triples("obj", 3, 1, "eats", "fish")
		};

		assertArrayEquals(expected_result, result.toArray(), "Sentence triples");
	}

	
	@Test
	void test_createDist() throws IOException {
		boolean reversed = false;
		List<Triples> ts1 = new ArrayList<Triples>();ts1.add(new Triples("a", 1, 2));
		List<Triples> ts2 = new ArrayList<Triples>();ts2.add(new Triples("a", 1, 2));
		double t_dist[][] = {{0.5, 0.5, 0.5}, {0.5, 0.5, 0.5}};
		SentenceAlignment dist = new DistanceMatrix(t_dist);
		MetricActivationCounter counters = new MetricActivationCounter();
		SentenceSimilarityTripleOverlapping sim = new SentenceSimilarityTripleOverlapping(counters,
				"test", new BufferedReader( new StringReader(
								"1\ttop	agent	att	dobj	iobj	obj-prep	pred	subj	subj-pac\n"
								+ "0.5	ok	adjmod	cc	co-%	dconj	dep	dep-%	sn-mod	sp-mod	sp-obj	subord-mod	vsubord	ador	aux\n"
								+ "0.5	last	dprep	dverb	es	espec	term\n"
								+ "%%#PATTERNS\n"
								+ "## perfect: COMPLETE\n"
								+ "(X,X,X) : 1.0\n"
								+ "## Partial NOLABEL\n"
								+ "(O,X,X) : 1.0\n"
								+ "## PARTIAL_NOMOD\n"
								+ "(X,X,O) : 1.0\n"
								+ "## partial NOHEAD\n"
								+ "#(X,O,X) : 0.7\n")));

		DistanceMatrix result = sim.createDist(reversed, ts1, ts2, dist);
		assertEquals(result.getDistance(reversed, 0, 0), Similarity.MAXVAL, "one perfect triple");
	}
	
	// TODO turn into junit
	 public void old_main_test() throws IOException {
			TripleMatchPattern t = new TripleMatchPattern("conf/triplesmatch.conf", new MetricActivationCounter(), "DEPHEAD", "DEPLABEL");
			boolean reversed = false;
			Triples x = new Triples("amod_de", 1, 2);
			Triples y = new Triples("prep_of", 1, 2);
			SentenceAlignment align = new AlignmentImpl(2, 2);
			align.setAligned(reversed, 0, 0, "");
			align.setAligned(reversed, 1, 1, "");
			System.err.println(x + " x " + y + "\nMATCH:" + t.match(reversed, x, y, align));
			y = new Triples("prep_oxx", 1, 2);
			System.err.println(x + " x " + y + "\nMATCH:" + t.match(reversed, x, y, align));
			x = new Triples("sbj_by", 1, 2);
			y = new Triples("sbj", 1, 2);
			System.err.println(x + " x " + y + "\nMATCH X X X sbj:" + t.match(reversed, x, y, align));
			System.err.println(t.getWeight("amod_de"));
		}
	
}
