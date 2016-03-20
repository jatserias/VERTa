package mt;

import java.io.PrintStream;

public class SentenceSimilarityDepScore extends SentenceSimilarityBase implements SentenceMetric {

	public	 SentenceSimilarityDepScore(MetricActivationCounter counters) {
		super(counters);
		// TODO Auto-generated constructor stub
	}

	@Override
	public SimilarityResult similarity(Sentence s1, Sentence s2,
			SentenceAlignment dist, PrintStream strace) {
		

		if(strace!=null) {
			strace.println("<depscore>");
			strace.println("<src score='"+s1.depscore+"'/>");
			strace.println("<trg score='"+s2.depscore+"'/>");
			strace.println("</depscore>");
		}
		
	
		double val= mt.SimilarityScores.getSym2(s1.depscore,s2.depscore );
//		System.err.println("SIM:"+val+" "+s1.depscore+" "+s2.depscore );
		return new SimilarityResult(val,val);
	}

	@Override
	public void dump(PrintStream strace) {
		// TODO Auto-generated method stub

	}

}