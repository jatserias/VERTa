package mt.nlp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import mt.nlp.Sentence;
import mt.nlp.Word;

class SentenceTest {

	@Test
	void test_constructor() {
		Sentence sentence = new Sentence();
		String words[] = {"the", "cat", "eats", "fish", "."};
		for(String word : words)
			sentence.add(new Word(word, word));
		assertEquals("the cat eats fish .", sentence.getText());
	}

}
