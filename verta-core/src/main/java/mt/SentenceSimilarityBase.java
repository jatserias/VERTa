package mt;

public abstract class SentenceSimilarityBase implements SentenceMetric{
	MetricActivationCounter counters;
	
	SentenceSimilarityBase(MetricActivationCounter counters){
		this.counters=counters;
	}
}
