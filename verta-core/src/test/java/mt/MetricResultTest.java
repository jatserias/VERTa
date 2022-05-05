package mt;

import org.junit.jupiter.api.Test;

import mt.core.MetricResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;

public class MetricResultTest {

	@Test
	public void test() throws IOException {

		double badDouble = Double.NaN; // obtain double from unit under test
		assertTrue(Double.isNaN(badDouble), "result should be an invalid value, but isn't");
		double p = 2 / 0.0;
		System.err.println(p);
		assertTrue(Double.isInfinite(p), "result should be an invalid value, but isn't");

		MetricResult m = new MetricResult();
		m.add("pp", 2, 0, 0);
		assertEquals(0.0, m.getF1(0));
		assertEquals(0.0, m.getPrec(0));
		assertEquals(0.0, m.getRec(0));
		m.add("pp2", 1, 1, 1);
		assertEquals(0.0, m.getF1(0));
		assertEquals(0.0, m.getPrec(0));
		assertEquals(0.0, m.getRec(0));
		assertEquals(1.0, m.getF1(1));
		assertEquals(1.0, m.getPrec(1));
		assertEquals(1.0, m.getRec(1));
		assertEquals(1.0 / 3.0, m.getWF1());
		assertEquals(1.0 / 3.0, m.getPrec());
		assertEquals(1.0 / 3.0, m.getRec());

	}
}
