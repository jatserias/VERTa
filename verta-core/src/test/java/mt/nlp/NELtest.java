package mt.nlp;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import mt.SentenceSimilarityNEL;
import mt.core.MetricActivationCounter;
import mt.core.SentenceAlignment;
import mt.nlp.Sentence;


public class NELtest {
	@Test
	public void happy_path_test() throws IOException {
		MetricActivationCounter c = new MetricActivationCounter();
		SentenceSimilarityNEL m = new mt.SentenceSimilarityNEL(c);
		SentenceAlignment dist = null;
		
		Sentence s2 = new Sentence();
		Sentence s1= new Sentence();
		Set<String> a= new HashSet<String>();
		a.add("Bush");
		s1.setNel(a);
		Set<String> b= new HashSet<String>();
		b.add("Bush");
		b.add("Bab");
		s2.setNel(b);
		
		assertEquals(1.0, m.similarity(s1, s1, dist, null).getF1(), 0.00000000001);
		
		assertEquals(0.666666666666666, m.similarity(s1, s2, dist, null).getF1(), 0.00000000001);
		assertEquals(1.0, m.similarity(s1, s2, dist, null).getPrec(), 0.00000000001);
		
		assertEquals(0.666666666666666, m.similarity(s2, s1, dist, null).getF1(), 0.00000000001);
		assertEquals(1.0, m.similarity(s2, s1, dist, null).getRec(), 0.00000000001);
	}
}
