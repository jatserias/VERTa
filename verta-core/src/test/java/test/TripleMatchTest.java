package test;


import java.io.IOException;
import java.io.PrintStream;


import org.junit.Test;

import mt.MetricActivationCounter;
import mt.ReaderCONLL;
import mt.Segment;

import mt.SentenceSimilarityTripleOverlapping;


public class TripleMatchTest {

	@Test
	public void test(){
		
	}
	
	public static void main(String args[]) throws IOException {
		
		MetricActivationCounter counters = new MetricActivationCounter();
		SentenceSimilarityTripleOverlapping s = new SentenceSimilarityTripleOverlapping(counters,"conf/triplesmatch.conf");
		String hypFilename="testdep_hyp.txt";
		String refFilename="testdep_ref.txt";
		ReaderCONLL refFile = new ReaderCONLL(refFilename);
		ReaderCONLL proposedFile = new ReaderCONLL(hypFilename);
		Segment seg1=proposedFile.readSegment();
		Segment seg2=refFile.readSegment();
		 boolean reversed=false;
		 PrintStream strace = System.err;
	//	SentenceAlignment align = new Sen
	//	s.findTriple(reversed, seg1, seg2, align, strace);
		
		
	}
}
