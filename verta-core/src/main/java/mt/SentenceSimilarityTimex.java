package mt;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.PTBTokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.TimeAnnotations.TimexAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.CoreMap;

public class SentenceSimilarityTimex extends SentenceSimilarityBase  implements SentenceMetric {


  
	public	 SentenceSimilarityTimex(MetricActivationCounter counters) {
		super(counters);
		// TODO Auto-generated constructor stub
	}


	/**
     * 1 iif the annotation are equal
     */
	@Override
	public SimilarityResult similarity(Sentence s1, Sentence s2,
			SentenceAlignment dist, PrintStream strace) {
			
		List<TimeExpressions> ne1=s1.timex;
		List<TimeExpressions> ne2=s2.timex;
		
		/**
		 * dump to trace
		 */
		if(strace!=null) {
		strace.println("<timex>");
		strace.println("<src>");
		for(TimeExpressions e1: ne1) {
			
			strace.println("<TIMEX3>"+e1+"</TIMEX3>");
			
		}
		strace.println("</src>");
		strace.println("<trg>");
		for(TimeExpressions e1: ne2) {
			
			strace.println("<TIMEX3>"+e1+"</TIMEX3>");
			
		}
		strace.println("</trg>");
		strace.println("</timex>");
		//
		}
		/**
		 * @TODO Compare  TIMEX annot in ne1 ne2
		 * no trace
		 */
		if(ne1.size()==0 && ne2.size()==0) {
			return SimilarityResult.perfect;
		}
			
		boolean found =false;
		int ne=0; int nef=0;
		for(TimeExpressions e1: ne1) {
			
			for(TimeExpressions e2: ne2) {
				found =(SimilarityTimeExpressions.similarity(e1,e2))>0;
				if(found) break;
			}
			if(found) nef++;
			ne++;
		}
		
		double prec= ne>0 ? ((1.0 * nef) / ne) : 0.0;
		
		ne=0;  nef=0;
		for(TimeExpressions e1: ne2) {
			
			for(TimeExpressions e2: ne1) {
				found =(SimilarityTimeExpressions.similarity(e1,e2))>0;
				if(found) break;
			}
			if(found) nef++;
			ne++;
		}
		double recall= ne>0 ? ((1.0 * nef) / ne) : 0.0;
		
		
		return new SimilarityResult(prec,recall);
	}

	
	@Override
	public void dump(PrintStream strace) {
		// TODO Auto-generated method stub
		
	}

}
