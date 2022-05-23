package mt.nlp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import mt.SentenceSimilarityTripleOverlapping;

class MakeTripleTest {

	@Test
	void test_default_columns_constructor_for_syntactic_deps() {
		Sentence s = new Sentence();
		s.add(new Word("1", "cat").setFeature(Triples.ID_NAME, "1").setFeature("DEPHEAD", "2").setFeature("DEPLABEL", "subj"));
		s.add(new Word("2", "eats").setFeature(Triples.ID_NAME, "2").setFeature("DEPHEAD", "_").setFeature("DEPLABEL", "_"));
		s.add(new Word("3", "fish").setFeature(Triples.ID_NAME, "3").setFeature("DEPHEAD", "2").setFeature("DEPLABEL", "obj"));
				
		Triples triples = SentenceSimilarityTripleOverlapping.MakeTriple(s, s.elementAt(0), "DEPHEAD", "DEPLABEL");
		// @TODO source and target re-reversed
		Triples expected_triple = new Triples("subj", 2, 1, "eats", "cats");
		
		assertEquals(expected_triple, triples, "Simple triples default column names");
	}
	
	@Test
	void test_columns_constructor() {
		Sentence s = new Sentence();
		String HEAD_COLUMN = "MYHEAD";
		String LABEL_COLUMN = "MYLABEL";
		
		s.add(new Word("1", "cat").setFeature(Triples.ID_NAME, "1").setFeature(HEAD_COLUMN, "2").setFeature(LABEL_COLUMN, "subj"));
		s.add(new Word("2", "eats").setFeature(Triples.ID_NAME, "2").setFeature(HEAD_COLUMN, "_").setFeature(LABEL_COLUMN, "_"));
		s.add(new Word("3", "fish").setFeature(Triples.ID_NAME, "3").setFeature(HEAD_COLUMN, "2").setFeature(LABEL_COLUMN, "obj"));
				
		Triples triples = SentenceSimilarityTripleOverlapping.MakeTriple(s, s.elementAt(0), HEAD_COLUMN, LABEL_COLUMN);
		// @TODO source and target re-reversed
		Triples expected_triple = new Triples("subj", 2, 1, "eats", "cats");
		
		assertEquals(expected_triple, triples, "Simple triples from arbitrary columns");
	}


}
