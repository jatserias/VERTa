package mt.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import mt.SentenceSimilarityTripleOverlapping;
import mt.nlp.Segment;
import mt.nlp.Sentence;
import mt.nlp.Triples;
import mt.nlp.Word;
import mt.nlp.io.CONLLformat;
import mt.nlp.io.ReaderCONLL;

public class TripleMatchTest {

	void really_old_main_test() throws IOException {
		assert (TriplesMatch.isPatternLabel("dep_%)"));
		assert (!TriplesMatch.isPatternLabel("dep%)"));
		assert (!TriplesMatch.isPatternLabel("dep_a)"));
		assert (TriplesMatch.getSubLabel("dep_a)").equals("a"));
		assert (TriplesMatch.getSubLabel("dep_de)").equals("de"));
		assert (TriplesMatch.getSubLabel("subj").equals("subj"));
		TriplesMatch tm = new TriplesMatch(new MetricActivationCounter());
		tm.getLabelMatch().put("dep_b#dep_a", 2.0);
		assert (tm.matchRules("dep_%", "dep_a"));
		assert (tm.matchRules("dep_a", "dep_%"));
		assert (tm.labelsMatch(new Triples("dep_%", 0, 0), new Triples("dep_a", 0, 0)));
		assert (tm.labelsMatch(new Triples("dep_a", 0, 0), new Triples("dep_%", 0, 0)));
		assert (!tm.labelsMatch(new Triples("kk_a", 0, 0), new Triples("dep_%", 0, 0)));
		assert (!tm.labelsMatch(new Triples("dep_a", 0, 0), new Triples("kk_%", 0, 0)));
		// System.out.println(tm.matchingScorer(new Triples("dep_%", 0, 0), new
		// Triples("dep_a", 0, 0), true, true, true));
		// System.out.println(tm.matchingScorer(new Triples("dep_b", 0, 0), new
		// Triples("dep_a", 0, 0), true, true, true));
	}

	public void old_main_test() throws Exception {

		MetricActivationCounter counters = new MetricActivationCounter();
		SentenceSimilarityTripleOverlapping s = new SentenceSimilarityTripleOverlapping(counters,
				"conf/triplesmatch.conf");
		String hypFilename = "testdep_hyp.txt";
		String refFilename = "testdep_ref.txt";
		CONLLformat fmt = new CONLLformat("conf/conll08.fmt");
		BufferedReader refFile = new BufferedReader(new FileReader(refFilename));
		BufferedReader proposedFile = new BufferedReader(new FileReader(hypFilename));

		Segment seg1 = ReaderCONLL.readSegment(proposedFile, fmt);
		Segment seg2 = ReaderCONLL.readSegment(refFile, fmt);
		boolean reversed = false;
		PrintStream strace = System.err;
		// SentenceAlignment align = new Sen
		// s.findTriple(reversed, seg1, seg2, align, strace);

	}

	@Test
	public void testVaris_from_refactor() throws IOException {

		Triples t1 = new Triples("s", 1, 2);
		Triples t2 = new Triples("s", 1, 2);

		Word w1 = new Word("1", "gato").setFeature("DEPLABEL", "sub").setFeature("DEPHEAD", "2");

		Word w2 = new Word("1", "come").setFeature("DEPLABEL", "_").setFeature("DEPHEAD", "_");

		Sentence sa1 = new Sentence();

		sa1.add(w1);
		sa1.add(w2);

		// double[][] align={ {1,0}, {0,1}};
		DistanceMatrix distances = new DistanceMatrix(sa1, sa1);
		distances.setDistance(false, 0, 0, 1, "test");
		distances.setDistance(false, 0, 1, 0, "test");
		distances.setDistance(false, 0, 0, 0, "test");
		distances.setDistance(false, 0, 0, 1, "test");
		SentenceAlignment nalign = new AlignmentImpl(distances.source_size, distances.target_size);
		new AlignmentBuilderFirstLeft2Rigth().build(false, nalign, distances);

		TriplesMatch triples_matcher = new TriplesMatch();
		triples_matcher.load("conf/triples.conf", new BufferedReader(new StringReader("#\n"
				+ "# Complete_WEIGHT	PARTIAL_NOMOD_WEIGHT      PARTIAL_NOHEAD_WEIGHT      PARTIAL_NOLABEL_WEIGHT \n"
				+ "#\n" + "1.0	0.8	0.7	0.7\n" + "#\n" + "# Label matching rules\n" + "#\n" + "# label - label weight\n"
				+ "#\n" + "amod	prep_of	1.0\n" + "nsubj	agent	1.0\n" + "")));
		assertTrue(triples_matcher.match(false, t1, t2, nalign).score > 0, "btest1");

		String[] words = { "the", "cat", "eats", "fish", "." };
		String[] deps = { "mod:2", "subj:3", "_:_", "obj:2", "_:_" };
		String[] words2 = { "the", "dog", "eats", "fish", "." };

		Sentence s1 = new Sentence();
		int n = 0;
		for (String s : words) {
			Word w = new Word("" + n, s);
			String[] buff = deps[n++].split(":");
			w.setFeature("DEPLABEL", buff[0]);
			w.setFeature("DEPHEAD", buff[1]);
			s1.add(w);
		}

		Sentence s2 = s1;
		n = 0;
		for (String s : words2) {
			Word w = new Word("" + n, s);
			String[] buff = deps[n++].split(":");
			w.setFeature("DEPLABEL", buff[0]);
			w.setFeature("DEPHEAD", buff[1]);
			s2.add(w);
		}
	}

	private static Stream<Arguments> generator() {
		return Stream.of(Arguments.of(true, true, true, TriplesMatch.COMPLETE_WEIGHT, "perfect match"),
				Arguments.of(true, true, false, TriplesMatch.PARTIAL_NOMOD_WEIGHT, "no mod match"),
				Arguments.of(true, false, true, TriplesMatch.PARTIAL_NOHEAD_WEIGHT, "no head match"),
				Arguments.of(true, false, false, TriplesMatch.NOMATCH, "only label match"),
				Arguments.of(false, true, true, TriplesMatch.PARTIAL_NOLABEL_WEIGHT, "only label match"),
				Arguments.of(false, true, false, TriplesMatch.NOMATCH, "no label match but mod amtch"),
				Arguments.of(false, false, true, TriplesMatch.NOMATCH, "no label match but head match"),
				Arguments.of(false, false, false, TriplesMatch.NOMATCH, "nothing match"));
	}

	@ParameterizedTest
	@MethodSource("generator")
	public void test_matchingScorer(boolean label_match, boolean head_match, boolean mod_match,
			MatchResult expected_result, String test_id) {
		MatchResult result = new TriplesMatch().matchingScorer(null, null, label_match, head_match, mod_match);
		assertEquals(expected_result, result, test_id);
	}

}
