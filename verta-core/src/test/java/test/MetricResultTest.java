package test;

import static org.junit.Assert.*;

import java.io.IOException;

import junit.framework.Assert;

import mt.MetricResult;

import org.junit.Test;

public class MetricResultTest {

	@Test
	public void test() throws IOException {
		
	       double badDouble = Double.NaN; // obtain double from unit under test
	        assertTrue("result should be an invalid value, but isn't", Double.isNaN(badDouble) );
//	        assertTrue(Double.NaN==Double.NaN);
            double p =  2 / 0.0;
            System.err.println(p);
//	        assertTrue("result should be an invalid value, but isn't", Double.isNaN(p) );
//	        assertTrue("result should be an invalid value, but isn't", Double.isNaN(new Double(p)) );
	        assertTrue("result should be an invalid value, but isn't", Double.isInfinite(p) );
//            assertTrue("result should be an invalid value, but isn't", Double.isNaN(p) );
		MetricResult m = new MetricResult();
		m.add("pp", 2, 0, 0);
		Assert.assertEquals(0.0,m.getF1(0));
		Assert.assertEquals(0.0,m.getPrec(0));
		Assert.assertEquals(0.0,m.getRec(0));
		m.add("pp2", 1, 1, 1);
		Assert.assertEquals(0.0,m.getF1(0));
		Assert.assertEquals(0.0,m.getPrec(0));
		Assert.assertEquals(0.0,m.getRec(0));
		Assert.assertEquals(1.0,m.getF1(1));
		Assert.assertEquals(1.0,m.getPrec(1));
		Assert.assertEquals(1.0,m.getRec(1));
		Assert.assertEquals( 1.0 / 3.0,m.getWF1());
		Assert.assertEquals(1.0 / 3.0,m.getPrec());
		Assert.assertEquals(1.0 / 3.0,m.getRec());
		m.add("pp3", 1.0,  0 /2  ,  0 / 1);
	}
}
