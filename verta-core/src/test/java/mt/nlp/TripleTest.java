package mt.nlp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import mt.nlp.Sentence;
import mt.nlp.Triples;
import mt.nlp.Word;

class TripleTest {

	@Test
	void test_constructor() {
		Sentence s = new Sentence();
		s.add(new Word("1", "cat").setFeature(Triples.ID_NAME, "1").setFeature(Triples.DEPHEAD_NAME, "2").setFeature(Triples.DEPLABEL_NAME, "subj"));
		s.add(new Word("2", "eats").setFeature(Triples.ID_NAME, "2").setFeature(Triples.DEPHEAD_NAME, "_").setFeature(Triples.DEPLABEL_NAME, "_"));
		s.add(new Word("3", "fish").setFeature(Triples.ID_NAME, "3").setFeature(Triples.DEPHEAD_NAME, "2").setFeature(Triples.DEPLABEL_NAME, "obj"));
				
		Triples triples = new Triples(s, s.elementAt(0));
		// @TODO source and target re-reversed
		Triples expected_triple = new Triples("subj", 2, 1, "eats", "cats");
		
		assertEquals(expected_triple, triples, "Simple triples");
	}

}
