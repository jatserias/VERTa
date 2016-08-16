package test;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Test;

import mt.CONLLformat;
import mt.MetricActivationCounter;
import mt.ReaderCONLL;
import mt.Segment;
import mt.SentenceSimilarityTripleOverlapping;


public class TripleMatchTest {

	@Test
	public void test(){
		
	}
	
	public static void main(String args[]) throws Exception {
		
		MetricActivationCounter counters = new MetricActivationCounter();
		SentenceSimilarityTripleOverlapping s = new SentenceSimilarityTripleOverlapping(counters,"conf/triplesmatch.conf");
		String hypFilename="testdep_hyp.txt";
		String refFilename="testdep_ref.txt";
		CONLLformat fmt = new CONLLformat("conf/conll08.fmt");
		BufferedReader refFile = new BufferedReader(new FileReader(refFilename));
		BufferedReader proposedFile = new BufferedReader(new FileReader(hypFilename));
		
		
		
		Segment seg1=ReaderCONLL.readSegment(proposedFile,fmt);
		Segment seg2=ReaderCONLL.readSegment(refFile,fmt);
		 boolean reversed=false;
		 PrintStream strace = System.err;
	//	SentenceAlignment align = new Sen
	//	s.findTriple(reversed, seg1, seg2, align, strace);
		
		
	}
}
