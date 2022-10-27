package mt.core;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class AlignmentBuilderTest {

    public static final double[][] m1 = {{1, 0, 0}, {0, 0, 0}, {0, 1, 1}, {0, 1, 1}};

    //public static final [] s1={}

    @Test
    public void happy_path_test() {
        AlignmentBuilder al = new AlignmentBuilderBestMatch();

        DistanceMatrix d = new DistanceMatrix(m1);
        SentenceAlignment s = new AlignmentImplSingle(m1.length, m1[0].length);
        al.build(s, d);

        int[] expected_s1_2_s2_alignment = {0, -1, 2, 1};
        assertArrayEquals(expected_s1_2_s2_alignment, s.getAlignment());


    }

}
