package mt;

import java.io.PrintStream;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;

public class SentenceSimilarityNEL extends SentenceSimilarityBase implements SentenceMetric {

	
	
	public	SentenceSimilarityNEL(MetricActivationCounter counters) {
		super(counters);
	}

	@Override
	public SimilarityResult  similarity(Sentence s1, Sentence s2,
			SentenceAlignment dist,  PrintStream strace) {
		
		@SuppressWarnings("unchecked")
		Collection<String> sres =CollectionUtils.intersection(s1.nel, s2.nel);
		
		
		
//		for(String e: s1.nel) System.err.println("S1:"+e);
//		for(String e: s2.nel) System.err.println("S2:"+e);
//		for(String e: sres) System.err.println("INT:"+e);
		
		
		//@TODO add matching or not mathing info
		if(strace!=null) {
			strace.println("<nel>");
			strace.println("<src>");
			for(String e1: s1.nel) {
				
				strace.println("<ne>"+e1+"</ne>");
				
			}
			strace.println("</src>");
			strace.println("<trg>");
			for(String e1: s2.nel) {
				
				strace.println("<ne>"+e1+"</ne>");
				
			}
			strace.println("</trg>");
			strace.println("</nel>");
			//
			}	
		//TODO fix counters
		//
		counters.increase(this.getClass().getCanonicalName(),1,true);
		counters.increase(this.getClass().getCanonicalName(),1,false);
		return  new SimilarityResult(sres.size(), s1.nel.size(),s2.nel.size());
	}

	@Override
	public void dump(PrintStream strace) {
		// TODO Auto-generated method stub
		
	}

}
