package mt;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;

import cern.colt.function.LongObjectProcedure;
import edu.berkeley.nlp.lm.NgramLanguageModel;
import edu.berkeley.nlp.lm.io.LmReaders;
import edu.berkeley.nlp.lm.util.Logger;

public class SentenceSimilarityLMS extends SentenceSimilarityBase implements SentenceMetric {
	enum LMOPT {
		SN_SN,SN_TN,SR_SR,SR_TR
	}
	
	LMOPT a;
	int W;
	
	public SentenceSimilarityLMS(MetricActivationCounter counters,String opts, String sweight) {
		super(counters);
		
		try {
		a=LMOPT.valueOf(opts);
		}catch(Exception e) {
		   System.err.println("Error in configuring opts param in "+this.getClass().getName());
		   System.err.println(e);
		   a = LMOPT.SN_SN;
		}
		
		try { 
			W = Integer.parseInt(sweight);
		}catch(Exception e) {
			   System.err.println("Error in configuring weigth param in"+this.getClass().getName());
			   System.err.println(e);
			   W=1;
		}
		
	}

	
	
	
	@Override
	public SimilarityResult similarity(Sentence s1, Sentence s2,
			SentenceAlignment dist, PrintStream strace) {
		
		float S_s=s1.lm;
		float S_r=(float) s1.lmn;
		float T_s=s2.lm;
		float T_r=(float) s2.lmn;
		
//JAB TODO TRACE
	
		switch (a) {
		case SN_SN:return  new SimilarityResult(W* S_s,W * S_s);
		case SN_TN:return  new SimilarityResult(W* S_s,W * T_s);
		case SR_SR:return  new SimilarityResult(W* S_r,W * S_r);
		case SR_TR:return  new SimilarityResult(W* S_r,W * T_r);
		
			default:return  new SimilarityResult(W* S_s,W * S_s);
		
			
		}
		
	}

	@Override
	public void dump(PrintStream strace) {
		// TODO Auto-generated method stub		
	}

	
	public static void main(String[] args) {
	
	  MetricActivationCounter mcounters = new MetricActivationCounter();;
	  
	  for(LMOPT la: LMOPT.values()) {
	  SentenceSimilarityLMS a = new  SentenceSimilarityLMS(mcounters,la.name(),"2");
	  Sentence s1= new Sentence();
	   
	  s1.lm=(float) -21.0;
	  s1.lmn=0.02;
	  Sentence s2= new Sentence();
	  s2.lm=(float) -42.0;
	  s2.lmn=0.04;
	  
	    System.err.println(la.name()+" "+a.similarity(s1, s2, null, System.err));
	  }
	  }
	}




