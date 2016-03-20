package mt;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class Segment { 

	List<Sentence> sentences;
 
 public Segment() {
	 super();
	 timex= new ArrayList<TimeExpressions>();
	 sentences = new Vector<Sentence>();
	 nel = new HashSet<String>();
 }
 
 public void addSen(Sentence s) { 
  
  sentences.add(s);
//  for(Word w: s) this.add(w);
//  woffset+=s.size();
 }
 
 /*@Override
 public Word get(int i) {
	int offset = -1;
	int s=0;
	while((i-offset)>sentences.get(s).size()) {
		offset += -sentences.get(s).size();
		++s;
	}
	return sentences.get(s).get(i-offset);
 }
 */
 
 public static Ngram[] ngram(Segment seg, int ngramSize) {
	 Vector<Ngram> res =new Vector<Ngram>();
	 for(Sentence s:seg.sentences)
	  for(Ngram ng : NgramMatch.ngram(s, ngramSize))
			  res.add(ng);
	 return res.toArray(Ngram.EMPTY_NGRAM);
 }
 
 ///return max size of the segment sentences
 public int segSize() {
	int max=0;
	for(Sentence s:sentences) {
		if(s.size()>max) max=s.size();
	}
	return max;
 }
 
// public static int compareNgrams(boolean reversed, Segment s1, Segment s2, SentenceAlignment align, PrintStream strace) {
//	 int nf1=0;
//		for(int ngramSize=2;ngramSize<=Math.min(s1.segSize(),s2.segSize());++ngramSize) {
//			strace.println("<ngrams s='"+ngramSize+"'>");
//			Ngram[] n1 = ngram(s1, ngramSize);
//			Ngram[] n2 = ngram(s2, ngramSize);
//			int nfx = NgramMatch.compareNgramsSameSize(reversed, n1, n2,  align,strace);
//			nf1 += nfx;
//			strace.println("<resngram s=\""+ngramSize+"\" common=\""+nfx+"\"/>");
//			strace.println("</ngrams>");
//		}
//		return nf1;
//	 
// }

public Sentence toSentence() {
	int woffset=0;
	Sentence res = new Sentence();
	
	/// propagate
	res.timex=timex;
	res.nel=nel;
	res.sentimentScore=sentimentScore;
	res.depscore=depScore;
	res.lm=lm;
	res.lmn=lmn;
	
	int nsen=1;
	for(Sentence s: sentences) {
	  for(Word w: s) {
		  Word w1 = new Word(w);
		     w1.id="x"+nsen+"="+(Integer.parseInt(w1.getFeature("ID"))+woffset);
		     w1.setFeature("ID",""+(Integer.parseInt(w1.getFeature("ID"))+woffset));
			 String head =w1.getFeature("DEPHEAD");
			 w1.setFeature("DEPHEAD","" + (head.startsWith("_") ?  head : (Integer.parseInt(w.getFeature("DEPHEAD")))+woffset));
			
		  res.add(w1);
	     }
	     nsen++;
	     woffset+=s.size();
	}
//	if(sentences.size()>1) {
//		res.dump(System.err);
//	}
	return res;
}

public void dump(PrintStream out) {
	out.println("<SEGMENT>");
	for(Sentence s:sentences)
		s.dump(out);
	out.println("</SEGMENT>");
}

List<TimeExpressions> timex;
public void addTimes(List<TimeExpressions> x) {
	timex=x;
}

private double sentimentScore;
public void addSentiment(double sentimentScore) {
	 this.sentimentScore=sentimentScore;	
	}
public double getSentiment() {
	 return this.sentimentScore;	
	}
private double depScore;
public void addDepScore(double depScore) {
 this.depScore= this.depScore==0 ? depScore : ((this.depScore+depScore)/2);	
}
public double  getDepScore() {
	 return this.depScore;	
}

 
Set<String> nel;

public void addNEL(String enel) {
	  this.nel.add(enel);
	}
	public Set<String>  getNEL() {
		 return this.nel;	
	}

/**
 * LM
 */
double lmn;
float lm;

public void addLM(float lm) { this.lm=lm;};
public float getLM() { return lm;}

public void addLMnorm(double r) {
	lmn=r;
}
public double getLMnorm(){ return lmn;}


}
