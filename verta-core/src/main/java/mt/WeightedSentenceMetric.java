package mt;

import java.io.PrintStream;

public class WeightedSentenceMetric  implements SentenceMetric {
  double weight;
  SentenceMetric metric;
  String name;
  
  public WeightedSentenceMetric(String name, double weight,SentenceMetric metric){
	  this.weight=weight;
	  this.metric=metric;
	  this.name=name;
  }
  
@Override
public SimilarityResult similarity(final Sentence s1, final Sentence s2, final SentenceAlignment dist,
		PrintStream strace) {
	return metric.similarity(s1, s2,  dist, strace);
}
  
public double getWeight() {return weight;}
public String getName() {
	return name;
}
public void dump(PrintStream strace) {
	metric.dump(strace);
}
}
