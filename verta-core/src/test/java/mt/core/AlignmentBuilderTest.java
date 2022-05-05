package mt.core;

import static org.junit.Assert.assertArrayEquals;

import org.junit.jupiter.api.Test;

public class AlignmentBuilderTest {

	public static final double[][] m1={
		{ 1, 0 , 0 },
		{ 0, 0,  0 },
		{ 0, 1 , 1 },
		{ 0, 1,  1 }
	};
	
	//public static final [] s1={}
	
	@Test
	public void happy_path_test() {
		AlignmentBuilder al = new AlignmentBuilderBestMatch();
		AlignmentBuilder ald = new AlignmentBuilderFirstLeft2Rigth();
		
		DistanceMatrix d = new DistanceMatrix(m1);
		SentenceAlignment s = new  AlignmentImpl(m1.length, m1[0].length);
		al.build(false, s, d);
		al.build(true, s, d);

		int[] expected_s1_2_s2_alignment = {0, -1,	2, 1};
		assertArrayEquals(expected_s1_2_s2_alignment, s.getAlignment(false));
		
		//TODO  if not initialized the sentence alignment do not modify the non aligned values (BAD)
		s = new  AlignmentImpl(m1.length,m1[0].length);
		ald.build(false, s, d);
		ald.build(true, s, d);

		int[] expected_s2_2_s1_alignment = {0, 2, 3};
		assertArrayEquals(expected_s2_2_s1_alignment, s.getAlignment(true));
			
	}

}
