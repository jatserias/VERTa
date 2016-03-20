package mt;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import edu.berkeley.nlp.lm.NgramLanguageModel;


public class SentenceSimilarityLMGoogle extends SentenceSimilarityBase implements SentenceMetric  {
	public static NgramLanguageModel<String> lm = null; 
	
	public SentenceSimilarityLMGoogle(MetricActivationCounter counters,
			String lmFile) {
		super(counters);
		if(lm==null) //lm =  LmReaders.readArrayEncodedLmFromArpa(lmFile, true); 
				lm = edu.berkeley.nlp.lm.io.LmReaders.readGoogleLmBinary(lmFile+"en.blm.gz", lmFile+"vocab_cs.gz");
		
	}
	

@Override
public SimilarityResult similarity(Sentence s1, Sentence s2,
		SentenceAlignment dist, PrintStream strace) {
	// TODO Auto-generated method stub
	List<String> sentence = new ArrayList<String>();
	for (Word w :s1 ) sentence.add(w.getText());
	
	float s = lm.scoreSentence(sentence);
	double r = Math.exp(s); //ScoreNormalizer.sigmoid((double) lm.scoreSentence(sentence));
	
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
}
