package mt.nlp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class WordTest {

	@Test
	void test_default_contructor() {
		Word word = new Word("1", "word");
		assertEquals(word.id, "1");
		assertEquals(word.getText(), "word");
		assertEquals(word.getFeature("WORD"), "word");
	}

	@Test
	void test_default_undefined_feature() {
		Word word = new Word("1", "word");
		assertEquals(word.getFeature("NOSE"), "NOPE");
	}
}
