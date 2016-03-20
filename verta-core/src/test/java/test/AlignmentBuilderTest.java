package test;

import static org.junit.Assert.*;

import mt.AlignmentBuilder;
import mt.AlignmentBuilderBestMatch;
import mt.AlignmentBuilderFirstLeft2Rigth;
import mt.AlignmentImpl;
import mt.DistanceMatrix;
import mt.SentenceAlignment;

import org.junit.Test;

public class AlignmentBuilderTest {

	public static final double[][] m1={
		{ 1, 0 , 0 },
		{ 0, 0,  0 },
		{ 0, 1 , 1 },
		{ 0, 1,  1 }
	};
	
	//public static final [] s1={}
	
	@Test
	public void test() {
		AlignmentBuilder al = new AlignmentBuilderBestMatch();
		AlignmentBuilder ald = new AlignmentBuilderFirstLeft2Rigth();
		
		DistanceMatrix d = new DistanceMatrix(m1);
		SentenceAlignment s = new  AlignmentImpl(m1.length,m1[0].length);
		al.build(false, s,d);
		al.build(true, s, d);
		System.err.println(s);
		
		//TODO  if not initilized the sentence aligment do not modifie de non aligned values (BAD)
		s = new  AlignmentImpl(m1.length,m1[0].length);
		ald.build(false, s,d);
		ald.build(true, s, d);
		System.err.println(s);
		
		
		//TODO  if not initilized the sentence aligment do not modifie de non aligned values (BAD)
				s = d;
		//		ald.build(false, s,d);
		//		ald.build(true, s, d);
				System.err.println(s);
				
	}

}
