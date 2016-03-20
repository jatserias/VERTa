package junit;

import java.util.HashSet;

import junit.framework.TestCase;

import mt.AlignmentBuilderFirstLeft2Rigth;
import mt.DistanceMatrix;
import mt.MTsimilarity;
import mt.MetricResult;
import mt.NgramMatch;
import mt.Sentence;
import mt.SentenceAlignment;
import mt.Word;
import mt.WordMetric;

import org.junit.Test;

import mt.Ngram;

public class NgramTest extends TestCase
{
	@Test
	public void testVaris() {
		
		String[] words= {"the", "cat","eats", "fish", "."};
		String[] words2= {"the", "dog","eats", "fish", "."};
	
		int[][] ngram1 ={{0,1}, {1,2},{2,3}, {3,4},
				         {0,1,2},{1,2,3},{2,3,4},
				         {0,1,2,3},{1,2,3,4},
				         {0,1,2,3,4}};
		
		HashSet<Ngram> ngramset1  = new HashSet<Ngram>();
		
		//crear ngrams
		MTsimilarity mt = new MTsimilarity("conf/test.conf");
		Sentence s1 = new Sentence();int n=0;
		for(String s:words) s1.add(new Word(""+(n++),s));
		Sentence s2 = s1;
		Sentence s3=new Sentence();n=0;
		for(String s:words2) s3.add(new Word(""+(n++),s));
		
		s1.dump(System.err);
		s2.dump(System.err);
		
		
	    //MetricResult r = mt.similarity(s1,s2, System.err);
	   
	    
		//double[][] dist= new double[words.length][words2.length];
		DistanceMatrix dist = new DistanceMatrix(s1,s1);
	    for(int i=0;i<words.length;++i) { dist.setDistance(false,i,i,1); dist.setDistance(true,i,i,1);}
		
	    MetricResult r = mt.similarity(s1, s2, System.err);
	    r.dist.dump(System.err);
	    
	    double ngramprec;
	    NgramMatch ngram = new NgramMatch("2");
	    
	    for(int i=2;i<s1.size();++i) {
	     ngramprec = NgramMatch.compareNgrams(i,true,s1,s1,r.dist, System.err);
	     System.err.println("i:"+i+":"+ngramprec+" #ngrams:"+MTsimilarity.sumatori(i,s1.size()));
	     assertEquals(ngramprec, MTsimilarity.sumatori(i,s1.size()),0.01);
	     double[] x = ngram.similarity(s1, s1, r.dist, System.err);
	     System.err.println("TRACE"+x[0]+":"+x[1]);
	     ngramprec = NgramMatch.compareNgrams(i,false,s1,s1,r.dist, System.err);
	     assertEquals(ngramprec, MTsimilarity.sumatori(i,s1.size()),0.01);
	    }
	  
	    
	    ngramprec = NgramMatch.compareNgrams(100,false,s1,s2,r.dist, System.err);
		System.err.println("PREC:"+ngramprec +":"+ MTsimilarity.sumatori(100,s1.size()));
		ngramprec = ngramprec / MTsimilarity.sumatori(100,s1.size());
		
		
		
		
		
		// we will need to reverese dist??
		double ngramrec = NgramMatch.compareNgrams(100,true,s2,s1,r.dist,System.err);
		ngramrec = ngramrec / MTsimilarity.sumatori(100,s2.size());
		System.err.println("PREC:"+ngramrec +":"+ MTsimilarity.sumatori(100,s1.size()));
		
		assertEquals(ngramprec, 1.0, 0.000001);
		assertEquals(ngramrec, 1.0, 0.000001);
	    
	    
	    /**
	     *  the dog eats fish vs the cat eats fish 
	     *  
	     *  common ngrams 
	     *  
	     *  s=2 : 1 / 3
	     *  s=3 : 0 / 2
	     *  s=4 : 0 / 1
	     *  
	     */
	    r = mt.similarity(s1, s3, System.err);
	    r.dist.dump(System.err);
	    SentenceAlignment align = dist; // new AlignmentBuilderFirstLeft2Rigth().build(dist);
		//OLD 
		//Integer align[] =  new Integer[Math.max(s1.size(), s2.size())];
		//TODO FIX JUNIT	
		//	for(int i=0;i<align.length;++i) align[i]=-1;
		//	double prec=mt.wms.get(0).sentenceSimilarity(align,dist,false, s1,s2,mt.wms.get(0));		
		//	for(int i=0;i<align.length;++i) align[i]=-1;
		//	double rec=sentenceSimilarity(align, dist,true,s1,s2,mt.wms.get(0));
		
		ngramprec = NgramMatch.compareNgrams(100,false,s1,s2,align, System.err);
		System.err.println("PREC:"+ngramprec +":"+ MTsimilarity.sumatori(100,s1.size()));
		ngramprec = ngramprec / MTsimilarity.sumatori(100,s1.size());
		// we will need to reverese dist??
		ngramrec = NgramMatch.compareNgrams(100,true,s2,s1,align,System.err);
		ngramrec = ngramrec / MTsimilarity.sumatori(100,s2.size());
		System.err.println("PREC:"+ngramrec +":"+ MTsimilarity.sumatori(100,s1.size()));
		
		assertEquals(ngramprec, 1.0, 0.000001);
		assertEquals(ngramrec, 1.0, 0.000001);
	}
}
