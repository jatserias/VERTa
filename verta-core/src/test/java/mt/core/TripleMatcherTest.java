package mt.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import mt.nlp.Sentence;
import mt.nlp.Triples;
import mt.nlp.Word;

public class TripleMatcherTest {

	void really_old_main_test() throws IOException {
		assert (TriplesMatcher.isPatternLabel("dep_%)"));
		assert (!TriplesMatcher.isPatternLabel("dep%)"));
		assert (!TriplesMatcher.isPatternLabel("dep_a)"));
		assert (TriplesMatcher.getSubLabel("dep_a)").equals("a"));
		assert (TriplesMatcher.getSubLabel("dep_de)").equals("de"));
		assert (TriplesMatcher.getSubLabel("subj").equals("subj"));
		TriplesMatcher tm = new TriplesMatcher();
		tm.setCounters(new MetricActivationCounter());
		tm.setHeadColumnName("DEPHEAD");
		tm.setLabelColumnName("DEPLABEL");
		tm.getLabelMatch().put("dep_b#dep_a", 2.0);
		assert (tm.matchRules("dep_%", "dep_a"));
		assert (tm.matchRules("dep_a", "dep_%"));
		assert (tm.labelsMatch(new Triples("dep_%", 0, 0), new Triples("dep_a", 0, 0)));
		assert (tm.labelsMatch(new Triples("dep_a", 0, 0), new Triples("dep_%", 0, 0)));
		assert (!tm.labelsMatch(new Triples("kk_a", 0, 0), new Triples("dep_%", 0, 0)));
		assert (!tm.labelsMatch(new Triples("dep_a", 0, 0), new Triples("kk_%", 0, 0)));
	}

	@Test
	public void testFromRefactoredLegacy() throws IOException {

		Triples t1 = new Triples("s", 1, 2);
		Triples t2 = new Triples("s", 1, 2);

		Word w1 = new Word("1", "gato").setFeature("DEPLABEL", "sub").setFeature("DEPHEAD", "2");

		Word w2 = new Word("1", "come").setFeature("DEPLABEL", "_").setFeature("DEPHEAD", "_");

		Sentence sa1 = new Sentence();

		sa1.add(w1);
		sa1.add(w2);

		// double[][] align={ {1,0}, {0,1}};
		DistanceMatrix distances = new DistanceMatrix(sa1, sa1);
		distances.setDistance(0, 0, 1, "test");
		distances.setDistance(0, 1, 0, "test");
		distances.setDistance(0, 0, 0, "test");
		distances.setDistance(0, 0, 1, "test");
		ISentenceAlignment nalign = new AlignmentImplSingle(distances.getRowSize(), distances.getColumnSize());
		new AlignmentBuilderFirstLeft2Rigth().build(nalign, distances);

		TriplesMatcher triples_matcher = new TriplesMatcher();
		triples_matcher.setHeadColumnName("DEPHEAD");
		triples_matcher.setLabelColumnName("DEPLABEL");

		TriplesMatcherLoader.load("conf/triples.conf", new BufferedReader(new StringReader("#\n"
				+ "# Complete_WEIGHT	PARTIAL_NOMOD_WEIGHT      PARTIAL_NOHEAD_WEIGHT      PARTIAL_NOLABEL_WEIGHT \n"
				+ "#\n" + "1.0	0.8	0.7	0.7\n" + "#\n" + "# Label matching rules\n" + "#\n" + "# label - label weight\n"
				+ "#\n" + "amod	prep_of	1.0\n" + "nsubj	agent	1.0\n" + "")));
		assertTrue(triples_matcher.match(t1, t2, nalign).getScore() > 0, "btest1");

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

		Sentence s2 = new Sentence();;
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
		return Stream.of(Arguments.of(true, true, true, TriplesMatcher.COMPLETE_WEIGHT, "perfect match"),
				Arguments.of(true, true, false, TriplesMatcher.PARTIAL_NO_MOD_WEIGHT, "no mod match"),
				Arguments.of(true, false, true, TriplesMatcher.PARTIAL_NO_HEAD_WEIGHT, "no head match"),
				Arguments.of(true, false, false, TriplesMatcher.NO_MATCH, "only label match"),
				Arguments.of(false, true, true, TriplesMatcher.PARTIAL_NO_LABEL_WEIGHT, "only label match"),
				Arguments.of(false, true, false, TriplesMatcher.NO_MATCH, "no label match but mod amtch"),
				Arguments.of(false, false, true, TriplesMatcher.NO_MATCH, "no label match but head match"),
				Arguments.of(false, false, false, TriplesMatcher.NO_MATCH, "nothing match"));
	}

	@ParameterizedTest
	@MethodSource("generator")
	public void test_matchingScorer(boolean label_match, boolean head_match, boolean mod_match,
			MatchResult expected_result, String test_id) {

		TriplesMatcher matcher = new TriplesMatcher();
		matcher.setHeadColumnName("DEPHEAD");
		matcher.setLabelColumnName("DEPLABEL");

		MatchResult result = matcher.matchingScorer(null, null, label_match, head_match,
				mod_match);
		assertEquals(expected_result, result, test_id);
	}

	@Test
	public void testSerialize() throws JsonProcessingException {
		YAMLFactory f = new YAMLFactory();
		f.disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID);
		final ObjectMapper mapper = new ObjectMapper(f);
		TriplesMatcher matcher = new TriplesMatcher();
		matcher.setHeadColumnName("DEPHEAD");
		matcher.setLabelColumnName("DEPLABEL");

		String jsonDataString = mapper.writeValueAsString(matcher);
		System.err.println(jsonDataString);
	}

}
