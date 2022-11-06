package mt.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;


class BaseSimilarityTest {

	@Test
	void testSetGet() {
		BaseSimilarity similarity = new BaseSimilarity();
		similarity.setWeight(10);
		assertEquals(similarity.getWeight(), 10, 0.0000001, "base similarity set-get");
	}

	@Test
	void testDefaultWeigthIsMtSimilarityMAXVAL() {
		BaseSimilarity similarity = new BaseSimilarity();
		assertEquals(similarity.getWeight(), mt.core.Similarity.MAX_VAL, 0.0000001, "base similarity set-get");
	}
}
