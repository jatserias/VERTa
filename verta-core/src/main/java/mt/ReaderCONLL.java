package mt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * 
 * A simple class to read conll format (tab separated fields and empty line sepataed sentences)
 * 
 * field names are stored at conf/conll08.fmt
 * 
 * @author jordi
 *
 */
public class ReaderCONLL {
	
	//BufferedReader in;
	//final CONLLformat fmt;
	/**
	 * creates a reader that will read conll format file filename
	 * @param filename
	 * @throws FileNotFoundException
	 */
//	public ReaderCONLL(String filename, CONLLformat fmt) throws FileNotFoundException {
//		this.fmt=fmt;
//		in = new BufferedReader(new FileReader(filename));	     
//	}
	
	/**
	 * 
	 * @return the sentence read or null
	 * @throws Exception 
	 */
//	public Sentence read() throws Exception {
//		return read(in,fmt);
//	}
	
	public static Sentence read(BufferedReader sin, CONLLformat pfmt) throws Exception {
	    String buff;
	    Sentence sen = new Sentence();
	    int nw=1;
	    
	    while((buff=sin.readLine())!=null && (buff.trim().length()!=0)) {
	    	if(buff.startsWith("%%#")) {
	    		//offset=0; 
	    		nw=1;
	    	}
	    	else {
	    	String fields[] = buff.split("[ \t]+");
	    	if(fields.length != pfmt.featureNames.length) {
	    		throw new Exception("CONLL format error at line:"+buff+" found "+fields.length+" fields  but format has defined "+pfmt.featureNames.length);
	    	}
	    	Word w = new Word(String.format("%d", nw++));
	    	int fnum=0;
	    	for(String feat: pfmt.featureNames) {
	    		w.setFeature(feat,fields[fnum++]);
	    	}
	    	sen.add(w);
	    	}
	    }
	    //sen.offset=offset;
	    //offset += sen.size();
		if(sen.size()==0) return null;
		else return sen;
	}

	
	/**
	 * read a segment from a CONLL file
	 * @return
	 * @throws Exception 
	 */
	public static Segment readSegment(BufferedReader in,CONLLformat fmt)  {
	    String buff;
	    Sentence sen = new Sentence();
	    Segment seg = new Segment();
	    int nw=1;
	    try{ 
	    //ignore first %%#
	    buff=in.readLine();
	    while(buff!=null && buff.startsWith("%%#")) {
	    	processHeader(seg,buff);
	    	buff=in.readLine();
	    }
	    
	    if(buff==null ) return null;
	    // while((buff=in.readLine())!=null && !buff.startsWith("%%#")) {
	    do {
	    	//System.err.println("REad >"+buff+"<:"+buff.trim().length());	    		
	    	if(buff.trim().length()<=0) {
	    		if(sen.size()>0) seg.addSen(sen);
	    		sen = new Sentence(); nw=1;
	    	}
	    	else {
	    	String fields[] = buff.split("[ \t]+");
	    	if(fields.length != fmt.featureNames.length) {
	    		throw new Exception("CONLL format error at line:"+buff+" found "+fields.length+" fields  but format has defined "+fmt.featureNames.length);	
	    	   }
	    	  Word w = new Word(String.format("%d", nw++));
	    	  int fnum=0;
	    	  for(String feat: fmt.featureNames) {
	    		w.setFeature(feat,fields[fnum++]);
	        	}
	    	 sen.add(w);
	    	 }
	    } while((buff=in.readLine())!=null && !buff.startsWith("%%#"));
	    
	    // Add last sentence if unexpectaly got eof
	    if(sen.size()>0) seg.addSen(sen);
	    
		if(seg.segSize()==0) return null;
		else return seg;
	    }catch(Exception e) {
	    	e.printStackTrace();
	    	
	    }
	    return null;
	}
	
	/**
	 * add process header annotations
	 * @param buff
	 */
	private static void processHeader(Segment seg,String buff) {
		//timex headers
		if(buff.startsWith("%%#TIMEX")) {
			String fields[] = buff.split("[\t]"); 
			int start =Integer.parseInt(fields[1]);
			int end =Integer.parseInt(fields[2]);
			String date =fields[4];
			String type = fields[3];
			seg.timex.add(
			 new TimeExpressions(start, end, date, type)	
			);
		}
		else if(buff.startsWith("%%#SENTI")) {
			if(buff.split("[\t ]").length>1) seg.addSentiment(Double.parseDouble(buff.split("[\t ]")[1]));
		}
		else if(buff.startsWith("%%#DEP")) {
			if(buff.split("[\t ]*").length>0) seg.addDepScore(Double.parseDouble(buff.split("[\t ]")[1]));
		}
		else if(buff.startsWith("%%#NEL")) {
			String [] res =  buff.split("[\t ]");
			for(int i=1;i<res.length;++i){
			seg.nel.add(
			res[i]
			);
			}
		}else if(buff.startsWith("%%#LM")) {
			if(buff.split("[\t ]*").length>0) seg.addLM(Float.parseFloat(buff.split("[\t ]")[1]));

		}else if(buff.startsWith("%%#LMN")) {
			if(buff.split("[\t ]*").length>0) seg.addLMnorm(Double.parseDouble(buff.split("[\t ]")[1]));
		}
	}

	/**
	 * 
	 * @param featureName
	 * @return true iif featureName is a feature of the format
	 */
	public static boolean hasFeature(String featureName,CONLLformat pfmt) {
	    boolean found=false;
	    int i=0;
	    while (!found && i< pfmt.featureNames.length) {
	    	found=(pfmt.featureNames[i++].compareTo(featureName)==0);
	    }
		return found;
	}

	public static boolean hasFeatures(String[] features,final CONLLformat pfmt) {
	    boolean hold=true;
	    int i=0;
	    while (hold && i< features.length) {
	    	hold=hasFeature(features[i],pfmt);++i;
	    }
		return hold;
	}
	
	
	public static void writeHeader(){}
	public static void writeSegment(int i,PrintStream nout,final Segment seg, CONLLformat pfmt){
		nout.println("%%#"+"SEG"+"\t"+i);
		nout.println("%%#"+"DEP"+"\t"+seg.getDepScore());
		nout.println("%%#"+"SENTI"+"\t"+seg.getSentiment());
		for(TimeExpressions t : seg.timex) {
			nout.println("%%#"+"TIMEX"+"\t"+t.start+"\t"+t.end+"\t"+t.type+"\t"+t.date);
		}
		for(String t : seg.nel) {
			nout.println("%%#"+"NEL"+"\t"+t);
		}
		nout.println("%%#"+"LM"+"\t"+seg.getLM());
		nout.println("%%#"+"LMN"+"\t"+seg.getLMnorm());
		Sentence s = seg.toSentence();
		
		for(Word w: s) {
			int j=0;
			for(String feature:pfmt.featureNames) {
				if(j>0) nout.print("\t");
				nout.print(w.getFeature(feature));
				++j;
			}
			nout.println();
		}
		nout.println();
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception 
	 */
	
	public static void main(String[] args) throws Exception {
		
		BufferedReader buff = new BufferedReader(new FileReader("/Users/jordiatserias/Batallin/Minicorpus_100_sentences.desr.conll"));
		CONLLformat fmt=		new CONLLformat("conf/conll08.fmt");
		Sentence s;
		while((s=ReaderCONLL.read(buff, fmt))!=null) {	
			s.dump(System.out);
		}
	}

	
}
