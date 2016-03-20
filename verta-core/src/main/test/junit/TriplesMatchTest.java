package junit;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import mt.AlignmentBuilderFirstLeft2Rigth;
import mt.DistanceMatrix;
import mt.Ngram;
import mt.Sentence;
import mt.SentenceAlignment;
import mt.Triples;
import mt.TriplesMatch;
import mt.Word;
import mt.WordMetric;

import org.junit.Test;


public class TriplesMatchTest {

	@Test
	public void testVaris() {
		
		Triples t1 = new Triples();
		Triples t2= new Triples();
		
		t1.source=1;
		t1.label="s";
		t1.target=2;
		
		t2.source=1;
		t2.label="s";
		t2.target=2;
		
		
		
		Word w1 = new Word("1","gato");
		w1.setFeature("DEPLABEL", "sub");
		w1.setFeature("DEPHEAD", "2");
		
		Word w2 = new Word("1","come");
		w1.setFeature("DEPLABEL", "_");
		w1.setFeature("DEPHEAD", "_");
		
		Word w3 = new Word("1","perro");
		w1.setFeature("DEPLABEL", "sub");
		w1.setFeature("DEPLABEL", "2");
		
		Sentence sa1 = new Sentence();
		
		sa1.add(w1);
		sa1.add(w2);
		//double[][] align={ {1,0}, 				   {0,1}};
		DistanceMatrix align = new DistanceMatrix(sa1,sa1);
		align.setDistance(false,0,0,1);
		align.setDistance(false,0,1,0);
		align.setDistance(false,0,0,0);
		align.setDistance(false,0,0,1);	
		SentenceAlignment nalign = new AlignmentBuilderFirstLeft2Rigth().build(align);
		
		assertTrue("btest1", new TriplesMatch("conf/triples.conf").match(false, t1, t2, nalign)>0);
		
		
	
		
		String[] words= {"the", "cat","eats", "fish", "."};
		String[] deps = { "mod:2", "subj:3",  "_:_", "obj:2", "_:_"};
		
		String[] words2= {"the", "dog","eats", "fish", "."};
	  
		
		Sentence s1 = new Sentence();int n=0;
		for(String s:words) { 
			Word w = new Word(""+n,s);
			String[] buff = deps[n++].split(":");
			w.setFeature("DEPLABEL", buff[0]);
			w.setFeature("DEPHEAD", buff[1]);
			s1.add(w);
		}
		
		
		Sentence s2 = s1;
		Sentence s3=new Sentence();n=0;
		for(String s:words2)  {
			Word w = new Word(""+n,s);
			String[] buff = deps[n++].split(":");
			w.setFeature("DEPLABEL", buff[0]);
			w.setFeature("DEPHEAD", buff[1]);
			s2.add(w);
		}
	}
}
