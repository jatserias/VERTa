package mt.core;

public abstract class SentenceSimilarityBase implements SentenceMetric {

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
