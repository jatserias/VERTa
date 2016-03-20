package mt;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.berkeley.nlp.lm.NgramLanguageModel;
import edu.berkeley.nlp.lm.io.LmReaders;

/**
 * 
 * using a LM to score Sentence fluency (ref is not used)
 * 
 * TODO implement sigmoid over the difference between hyp and ref 
 * @author jordi
 *
 */
public class SentenceSimilarityLM extends SentenceSimilarityBase implements SentenceMetric {
	//public static final  String lmFile="/Users/jordi/development/spetia/lm.europarl-nc.en"; // news.3gram.en.lm";
	public static NgramLanguageModel<String> lm = null; 
	
	public SentenceSimilarityLM(MetricActivationCounter counters,String lmFile) {
		super(counters);
		if(lm==null) lm =  LmReaders.readArrayEncodedLmFromArpa(lmFile, true); 
				//edu.berkeley.nlp.lm.io.LmReaders.readGoogleLmBinary(lmFile, "LM/vocab_cs.gz");
		
	}

	
	
	
	@Override
	public SimilarityResult similarity(Sentence s1, Sentence s2,
			SentenceAlignment dist, PrintStream strace) {
		// TODO Auto-generated method stub
		List<String> sentence = new ArrayList<String>();
		for (Word w :s1 ) sentence.add(w.getText());
		
		float s = lm.scoreSentence(sentence);
		double r =Math.exp(s); //ScoreNormalizer.sigmoid((double) lm.scoreSentence(sentence));
		
		if(strace!=null) {
			strace.println("<lm>");
			strace.println("<score>"+s+"</score>");
			strace.println("<escore>"+r+"</escore>");
			strace.println("</lm>");
		}
		
		assert(r>=0 && r<=1);
		return  new SimilarityResult(s,s);
	}

	@Override
	public void dump(PrintStream strace) {
		// TODO Auto-generated method stub		
	}

	
	public static void main(String[] args) {
	
	  MetricActivationCounter mcounters = new MetricActivationCounter();;
	  String file= 
			  // "/Users/jordi/yprojects/MTmetricsELi/LM/en.blm.gz"; 
			 // "LM/spetia/lm.europarl-nc.en.gz";  
			  "LM/spetia/news.3gram.en.lm.gz";
	  SentenceSimilarityLM a = new  SentenceSimilarityLM(mcounters,file);
	  String[][] sws =  { {"The", "illegal", "deal", "."}, {"The", "cat", "eats", "fish", "."}};
	  for (String[] ws:sws) {
	    System.err.println(a.lm.scoreSentence(Arrays.asList(ws)));
	  }
//	  Sentence s2 = new Sentence();
//	  SentenceAlignment dist = null;
//	  Sentence s1 = new Sentence();
//	  a.similarity(s1, s2, dist , System.err);
	}
}
