package mt;

import java.io.PrintStream;

public class SentenceSimilaritySentiment extends SentenceSimilarityBase implements SentenceMetric {

	public	 SentenceSimilaritySentiment(MetricActivationCounter counters) {
		super(counters);
		// TODO Auto-generated constructor stub
	}

	@Override
	public SimilarityResult similarity(Sentence s1, Sentence s2,
			SentenceAlignment dist, PrintStream strace) {
		

		if(strace!=null) {
			strace.println("<senti>");
			strace.println("<src score='"+s1.sentimentScore+"'/>");
			strace.println("<trg score='"+s2.sentimentScore+"'/>");
			strace.println("</senti>");
		}
		//double[] res = new double[2];
		//res[0] = ( 4 - Math.abs(s1.sentimentScore - s2.sentimentScore) )/ 4;
		//res[1] = res[0];
		//return res;
		double val=( 4 - Math.abs(s1.sentimentScore - s2.sentimentScore) )/ 4;
		return new SimilarityResult(val,val);
	}

	@Override
	public void dump(PrintStream strace) {
		// TODO Auto-generated method stub

	}

}
