package test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import mt.MetricActivationCounter;
import mt.Sentence;
import mt.SentenceAlignment;
import mt.SentenceSimilarityNEL;

import org.junit.Test;

public class NELtest {
	@Test
	public void test() throws IOException {
		MetricActivationCounter c = new MetricActivationCounter();
		SentenceSimilarityNEL m = new mt.SentenceSimilarityNEL(c );
		SentenceAlignment dist = null;
		
		Sentence s2 = new Sentence();
		Sentence s1= new Sentence();
		Set<String> a= new HashSet<String>();
		a.add("Bush");
		s1.nel = a;
		Set<String> b= new HashSet<String>();
		b.add("Bush");
		b.add("Bab");
		s2.nel = b;
		System.err.println(m.similarity(s1, s1, dist, null));
		System.err.println(m.similarity(s1, s2, dist, null));
		System.err.println(m.similarity(s2, s1, dist, null));
	}
}
