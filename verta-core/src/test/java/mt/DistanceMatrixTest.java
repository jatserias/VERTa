package mt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import mt.core.DistanceMatrix;

class DistanceMatrixTest {

	private static Stream<Arguments> generator() {
		 double[][] e1 = {{1, 2}, {3, 4}};
		 int [] h1 = {1, 1};
		 int [] v1 = {1, 1};
		 
		 double[][] e2 = {
				 {1, 2, 3}, 
				 {3, 4, 5}, 
				 {6, 1, 2}};
		 int [] h2 = {2, 2, 0};
		 int [] v2 = {2, 1, 1};
		 
		 return Stream.of(
		   Arguments.of(e1, h1, v1),  Arguments.of(e2, h2, v2)
		  );
		}
		
	@ParameterizedTest
	@MethodSource("generator")
	void test_bestMatch(double[][] table, int[] bestH, int[] bestV) {
		DistanceMatrix distances = new DistanceMatrix(table);
		
		for(int column: IntStream.range(0, bestH.length).toArray())
			assertEquals(bestH[column], distances.bestMatchH(column), String.format("best H %d", column));
		
		for(int row: IntStream.range(0, bestV.length).toArray())
			assertEquals(bestV[row], distances.bestMatchV(row), String.format("best V %d", row));
	}
	
	
	@Test
	void test_bestMatchManual() {
		/**
		 *  1 2
		 *  3 4
		 */
		DistanceMatrix distances = new DistanceMatrix(2, 2);
		distances.setDistance(0, 0, 1, "test");
		distances.setDistance(0, 1, 2, "test");
		distances.setDistance(1, 0, 3, "test");
		distances.setDistance(1, 1, 4, "test");
		
		assertEquals(distances.bestMatchH(0), 1, "MatchH 0");
		assertEquals(distances.bestMatchH(1), 1, "MatchH 1");
		assertEquals(distances.bestMatchV(0), 1, "MatchV 0");
		assertEquals(distances.bestMatchV(1), 1, "MatchV 1");
	}
	
	
	@Test 
	void test_distance_simple_set_get() {
		DistanceMatrix distances = new DistanceMatrix(2, 2);
		distances.setDistance(0, 1, 0.99, "my test");
		assertEquals(0.99, distances.getDistance(0, 1),  0.00001, "simple set-get");
	}
	
	@Test
	void test_get_distance_no_initilized() {
		DistanceMatrix distances = new DistanceMatrix(2, 2);
		
		assertEquals(distances.getDistance(1, 0), 0, 0.00001, "no initialized get");
	    assertEquals(distances.getDistance(0, 1), 0, 0.00001, "no initialized reverse get");
	}
	
	@Test
	void test_get_distance_out_of_bounds() {
		DistanceMatrix distances = new DistanceMatrix(2, 2);
		try {
			assertEquals(distances.getDistance(3, 2), 0, 0.00001, "out of bounds distance get");
	    } catch (Exception e) {
	        assertNotNull(e);
	    }
		
		try {
			assertEquals(distances.getDistance(0, 4), 0, 0.00001, "out of bounds distance reverse get");
		} catch (Exception e) {
	        assertNotNull(e);
	    }	
	}
	
}
