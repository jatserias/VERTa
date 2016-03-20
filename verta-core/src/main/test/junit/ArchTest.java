package junit;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

import mt.*;

import org.junit.Test;

/**
 * there are some bugs on delaing with:
 * 
 * empty word: a word must have "WORD" attributr
 * empty sentences: a sentence must have at least a word (dist[][])
 * 
 * @author jordiatserias
 *
 */
public class ArchTest extends TestCase{
	@Test
	public void testVaris() throws FileNotFoundException {
		
		MTsimilarity mt = new MTsimilarity("conf/test/test_mt_1.conf");
		//Similarity ok = new SimilarityPerfect();
		//Similarity ko =  new SimilarityUnsimilar();
		//SentenceSimilarity = new SentenceSimilarityPerfect();
		Word w = new Word("hola","hola");
		System.err.println("WSYM:"+mt.wms.get(0).similarity(w, w));
		mt.dump(System.err);
		Sentence s1 = new Sentence();
		s1.add(w);
		Sentence s2 = new Sentence();
		s2.add(w);
		PrintStream strace = new PrintStream(new FileOutputStream("/dev/null"));
		MetricResult res = mt.similarity(s1, s2, strace);
		//for(double d:res) {System.err.println(d);}
		assertEquals("1 check prec",res.getPrec(), 0.1);
		assertEquals("1 check rec",res.getRec(), 0.1);
		assertEquals("1 check f1",res.getF1(), 0.1,0.0000000001);
		
		
		mt = new MTsimilarity("conf/test/test_mt_2.conf");
	    res = mt.similarity(s1, s2, strace);
		//for(double d:res) {System.err.println(d);}
		assertEquals("1 check prec",res.getPrec(), 0.21);
		assertEquals("1 check rec",res.getRec(), 0.21);
		assertEquals("1 check f1",res.getF1(), 0.21,0.0000000001);
		
	}
	

}
