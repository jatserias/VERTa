package mt.core;

import java.util.HashMap;
import java.util.Map;

public class MetricActivationCounter {

	Map<String, Integer> counts;

	public MetricActivationCounter() {
		counts = new HashMap<>();
	}

	public void increase(String counter, int score) {
		if (counts.containsKey(counter)) {
			counts.put(counter, counts.get(counter) + score);
		} else {
			counts.put(counter, score);
		}
	}

	public void dump() {
		for (String k : counts.keySet()) {
			System.err.println(k + ":" + counts.get(k));
		}

	}

}
