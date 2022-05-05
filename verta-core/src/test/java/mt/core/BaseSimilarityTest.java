package mt.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;


class BaseSimilarityTest {

	@Test
	void test_set_get() {
		BaseSimilarity similarity = new BaseSimilarity();
		similarity.setWeight(10);
		assertEquals(similarity.getWeight(), 10, 0.0000001, "base similarity set-get");
	}

	@Test
	void test_default_weigth_is_mt_Similarity_MAXVAL() {
		BaseSimilarity similarity = new BaseSimilarity();
		assertEquals(similarity.getWeight(), mt.core.Similarity.MAXVAL, 0.0000001, "base similarity set-get");
	}
}
