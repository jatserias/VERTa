package mt.nlp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class SegmentTest {

	
	@Test
	public void testAddSingleSentence() {
		Sentence s = new Sentence();
	    Segment segment = new Segment();
	    assertEquals(0, segment.getSentences().size());
		segment.getSentences().add(s);
		assertEquals(1, segment.getSentences().size());
	}

	@Test
	public void testNgramSize0() {
	
		try {
			Segment segment = new Segment();
			Sentence sentence = new Sentence();
			sentence.add(new Word("1", "a"));
			Segment.ngram(segment, 0);
	    } catch (Exception e) {
	        assertNotNull(e);
	    }
	}
	
	@Test
	public void testNgramHappyPath() {
		Segment segment = new Segment();
		Sentence sentence = new Sentence();
		sentence.add(new Word("1", "a"));
		sentence.add(new Word("2", "b"));
		segment.addSen(sentence);
		Ngram[] expected_ngram_1_result = {new Ngram(0,1), new Ngram(1,1)};
		assertArrayEquals(Segment.ngram(segment, 1), expected_ngram_1_result, "Ngram Single sentence ngram 1");
		Ngram[] expected_ngram_2_result = {new Ngram(0,2)};
		assertArrayEquals(Segment.ngram(segment, 2), expected_ngram_2_result, "Ngram Single sentence ngram 2");
		
	}

	@Test
	public void testSegmentSize() {
		Segment segment = new Segment();
		Sentence sentence = new Sentence();
		sentence.add(new Word("1", "a"));
		segment.addSen(sentence);
		assertEquals(1, segment.segSize(), "Segment size one sentence");
		Sentence sentence_2 = new Sentence();
		sentence_2.add(new Word("1", "a"));
		sentence_2.add(new Word("2", "b"));
		segment.addSen(sentence_2);
		assertEquals(2, segment.segSize(), "Segment size two sentences");
	}

}
