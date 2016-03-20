package mt;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.PTBTokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.time.TimeAnnotations.TimexAnnotations;
import edu.stanford.nlp.util.CoreMap;

public class SentenceSimilarityTimexOnthefly extends  SentenceSimilarityTimex {

AnnotationPipeline pipeline;
	/*
	 * creates the annotator
	 */
    public 	SentenceSimilarityTimexOnthefly(MetricActivationCounter counters) {
		super(counters);
    	System.setProperty("pos.model","english-left3words-distsim.tagger");
     pipeline = new AnnotationPipeline();   
 	 pipeline.addAnnotator(new PTBTokenizerAnnotator(false));
     pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
     pipeline.addAnnotator(new POSTaggerAnnotator(false));  
     Properties opt = new Properties();
     opt.setProperty("customAnnotatorClass.sutime","edu.stanford.nlp.time.TimeAnnotator");
     opt.setProperty("sutime.rules","conf/defs.sutime.txt, conf/english.sutime.txt");
     pipeline.addAnnotator(new TimeAnnotator("sutime",opt)); // assuming "sutime";
	}
    
    /**
     * 1 iif the annotation are equal
     */
	@Override
	public SimilarityResult  similarity(Sentence s1, Sentence s2,
			SentenceAlignment dist, PrintStream strace) {
		s1.timex=generateTIMEX(s1);
		s2.timex=generateTIMEX(s2);
		return super.similarity(s1, s2,dist, strace);
	  
	}

	public  List<TimeExpressions> generateTIMEX(Sentence s) {
	    return generateTIMEX(s.getText());
	}
	
	public  List<TimeExpressions> generateTIMEX(String s) {
		List<TimeExpressions> res = new ArrayList<TimeExpressions>();
		String docDate="2012-9-19";
		//for(String s:sentences) {
		Annotation annotation = new Annotation(s);
		annotation.set(CoreAnnotations.DocDateAnnotation.class, docDate);
		pipeline.annotate(annotation);
		for(CoreMap t: annotation.get(TimexAnnotations.class)){ 
			int start=t.get(edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation.class);
	       	int end=t.get(edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetEndAnnotation.class);
	       	Timex tdate=t.get(edu.stanford.nlp.time.TimeAnnotations.TimexAnnotation.class);
	       	String date=tdate.value();
	       	String type = tdate.timexType();
	       	System.err.println("date:"+date);
	       	System.err.println("type:"+type);
	       	System.err.println("alt:"+tdate.altVal());
	       	System.err.println("txt:"+tdate.text());
	      
	       	res.add(new TimeExpressions(start,end,date,type));
		}
		return res;
	}

	@Override
	public void dump(PrintStream strace) {
		// TODO Auto-generated method stub		
	}
	
	public static void main (String[] args) throws FileNotFoundException {
		MetricActivationCounter counters = new MetricActivationCounter();
		SentenceSimilarityTimexOnthefly s = new SentenceSimilarityTimexOnthefly(counters);
		List<TimeExpressions> x = s.generateTIMEX("This series of events in the Beba province started burning five churches in the 3rd February .");
		for(TimeExpressions t:x) {
			System.err.println(t);
		}
		/**
		String hypFilename="/Users/jordi/Dropbox/eli/verta2.0/corpus/adequacy2/100_sentences_sys01.jtag";
		ReaderCONLL proposedFile = new ReaderCONLL(hypFilename);
		Segment seg;
		while((seg=proposedFile.readSegment())!=null) {
		 Sentence sen=seg.toSentence();
		 List<TimeExpressions> p = s.generateTIMEX(sen);
		 
		}
		8**/
	}
}
