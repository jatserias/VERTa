package mt.core;

import java.util.logging.Logger;


public abstract class SentenceSimilarityBase implements SentenceMetric {
	
	protected static Logger LOGGER = Logger.getLogger(SentenceSimilarityBase.class.getName());
	
	private MetricActivationCounter counters;

	public SentenceSimilarityBase(MetricActivationCounter counters) {
		this.setCounters(counters);
	}

	public MetricActivationCounter getCounters() {
		return counters;
	}

	public void setCounters(MetricActivationCounter counters) {
		this.counters = counters;
	}
}
