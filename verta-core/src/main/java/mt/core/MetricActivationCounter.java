package mt.core;

import java.util.HashMap;
import java.util.Map;

public class MetricActivationCounter {

	Map<String, Integer> counts;

	public MetricActivationCounter() {
		counts = new HashMap<String, Integer>();
	}

	public void increase(String counter, double score) {
		if (counts.containsKey(counter)) {
			counts.put(counter, counts.get(counter) + 1);
		} else {
			counts.put(counter, 1);
		}
	}

	public void dump() {
		for (String k : counts.keySet()) {
			System.err.println(k + ":" + counts.get(k));
		}

	}

}
