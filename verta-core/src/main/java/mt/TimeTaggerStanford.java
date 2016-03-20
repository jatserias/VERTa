package mt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;


import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.PTBTokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.time.SUTime.TimeIndex;
import edu.stanford.nlp.time.SUTimeMain;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.TimeAnnotations.TimexAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.TypesafeMap.Key;

public class TimeTaggerStanford {
	
	 AnnotationPipeline pipeline;
	
    public 	TimeTaggerStanford() {
    	System.setProperty("pos.model","english-left3words-distsim.tagger");
     pipeline = new AnnotationPipeline();   
 	 pipeline.addAnnotator(new PTBTokenizerAnnotator(false));
     pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
     pipeline.addAnnotator(new POSTaggerAnnotator(false));  
     Properties opt = new Properties();
     opt.setProperty("customAnnotatorClass.sutime","edu.stanford.nlp.time.TimeAnnotator");
     opt.setProperty("sutime.rules","src/main/resources/defs.sutime.txt, src/main/resources/english.sutime.txt");
     pipeline.addAnnotator(new TimeAnnotator("sutime",opt)); // assuming "sutime";
	}
    
	
	public List<CoreMap> tag(String s) {
		// we shoudl set the publishing date
		System.err.println("Tagging..");
		String docDate="2012-9-19";
		//for(String s:sentences) {
		Annotation annotation = new Annotation(s);
		System.err.println("Tagging.."+s);
		annotation.set(CoreAnnotations.DocDateAnnotation.class, docDate);
		pipeline.annotate(annotation);
		System.err.println("ANO"+annotation);
		 List<CoreMap> timexes = annotation.get(TimexAnnotations.class);
	      for (CoreMap t:timexes) {
	    	  for(Class k: t.keySet()) {
	    		 System.err.println(k+"="+t.get(k));
	    	 }
	       //do something;
	    	  System.err.println(t.get(edu.stanford.nlp.time.TimeAnnotations.TimexAnnotation.class));
	    	  System.err.println("TIMEX"+t);
	      }
		//}
		return timexes;
	}

	public static void main(String args[]) throws Exception {
		List<String> sens = new ArrayList<String>();
		sens.add("Last summer, they met every Tuesday afternoon, from 1:00 pm to 3:00 pm.");
		sens.add("Today is the first day of a new year.");
		TimeTaggerStanford test = new TimeTaggerStanford();
		 List<CoreMap> x = test.tag(sens.get(0));
		
		
		  	
			ReaderCONLL proposedFile = new ReaderCONLL("data/eli/100_sentences_sys01tag-new");
			
			try {
				Sentence s = proposedFile.read();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
}
