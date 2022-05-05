package mt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Vector;

import mt.core.AlignmentBuilderBestMatch;
import mt.core.AlignmentImpl;
import mt.core.DistanceMatrix;
import mt.core.MatchResult;
import mt.core.MetricActivationCounter;
import mt.core.SentenceAlignment;
import mt.core.SentenceMetric;
import mt.core.SentenceSimilarityBase;
import mt.core.SimilarityResult;
import mt.core.TripleMatchPattern;
import mt.core.TriplesMatch;
import mt.nlp.Sentence;
import mt.nlp.Triples;
import mt.nlp.Word;
import verta.xml.XMLFormater;

/**
 * 
 * 
 * compare to sentences by the overlapping of the triplets generated
 * 
 * generate a distance metrix i /j  then apply the alignment strategy
 * 
 * @TODO enforce that the target triple is taken only once
 * 
**/
public class SentenceSimilarityTripleOverlapping extends SentenceSimilarityBase implements SentenceMetric {


	public static boolean FILTER_TOP = false;
	
	/// USE OLD MATCHING (LREC version)
	static boolean USE_OLD=false;
	
	/// matching triples
	TriplesMatch tmatch;
	
	/**
	 * we should parametrice
	 * @param configfile
	 * @throws IOException 
	 */
	public SentenceSimilarityTripleOverlapping(MetricActivationCounter counters, String configFile) throws IOException{
		super(counters);
		tmatch= USE_OLD ? new TriplesMatch(configFile, counters) : new TripleMatchPattern(configFile, counters); 
	}
	
	public SentenceSimilarityTripleOverlapping(MetricActivationCounter counters, String configFile, BufferedReader config) throws IOException{
		super(counters);
		tmatch = USE_OLD ? new TriplesMatch(counters) : new TripleMatchPattern(counters); 
		tmatch.load(configFile, config);
	}
	
	double compare(boolean reversed,final Sentence s1, final Sentence s2, final SentenceAlignment alignment, PrintStream strace){
		 List<Triples> ts1 = tripleGenerator(s1);
		 List<Triples> ts2 = tripleGenerator(s2);
		 return compare(reversed,ts1,ts2,alignment,strace);
	}

	/**
	 * Gets the best triple that match
	 * 
	 * @param reversed
	 * @param n1
	 * @param s2
	 * @param align
	 * @param strace
	 * @return
	 */
	public  double findTriple(boolean reversed, final Triples n1, final Triples[] s2, final SentenceAlignment align, PrintStream strace) {
		
		double res=0.0;
		Triples btrip=null;
		
		for(Triples ts2: s2) {
			double cres= tmatch.match(reversed,n1, ts2, align).score;
			if(cres>res)  {
				 btrip= ts2;
				 res=cres;
			}
		}
		
		
		if(MTsimilarity.DUMP)  {		
			xml_dump_triples(n1, strace, res, btrip);
		}
		return res;
	}

	private static void xml_dump_triples(final Triples n1, PrintStream strace, double res, Triples btrip) {
		strace.println("<trip  sc=\""+res+"\">");
		strace.println("<src>"+XMLFormater.encodeXMLString(n1.toString())+"</src>");
		if(btrip!=null){ 
			strace.println("<trg>"+XMLFormater.encodeXMLString(btrip.toString())+"</trg>");
		} 
		else {strace.println("<trg>NO MATCH</trg>");}
		strace.println("</trip>");
	}
	
	
	private double compare(boolean reversed, final List<Triples> ts1, final List<Triples> ts2,final  SentenceAlignment alignment, PrintStream strace) {
		  // That may no be simetric if align is not simetric
		  double res=0.0;
		  
		 
		  for(Triples n1:ts1.toArray(new Triples[0])){
			  
			  res +=(findTriple(reversed,n1,ts2.toArray(new Triples[0]),alignment,strace));
			  
		  }
		  // 
		  return res;
	}

	/**
	 * Reconstruct/read triples/call parser
	 * @param s1 sentence
	 * @return a list of triples 
	 */
	static List<Triples> tripleGenerator(final Sentence s1) {
		List<Triples> res = new Vector<Triples>();
		for(Word w: s1) {
			Triples t =  new Triples(s1, w);
			
			if(FILTER_TOP || t.getTarget()>=1) res.add(t);
		}
		return res;
	}

	double getMax() {
		return 0;
	}
	
	
	@Override
	public  SimilarityResult similarity(
			final Sentence proposedSentence,
			final Sentence referenceSentence, 
			final SentenceAlignment dist, PrintStream strace) {	
		List<Triples> ts1 = tripleGenerator(proposedSentence);		
		List<Triples> ts2 = tripleGenerator(referenceSentence);
		SentenceAlignment align = new AlignmentImpl(ts1.size(), ts2.size());

		DistanceMatrix d = createDist(false,ts1, ts2, dist);
		
		//TODO  Normalization is going to hell 
		double sum=0.0;
		for(Triples t:ts1) {
			sum += tmatch.getWeight(t.label);
		}
		double prec = sum >0 ? INsimilarity(false, align, ts1, ts2, d,strace) / sum : 0.0;
		
		d = createDist(true,ts1, ts2, dist);
		
		//TODO  Normalization is going to hell 
		sum=0.0;
		for(Triples t:ts2) {
			sum +=tmatch.getWeight(t.label);
		}
	    double recall= sum >0 ? INsimilarity(true, align, ts2, ts1, d,strace) / sum  : 0.0;
		return new SimilarityResult(prec,recall);
	}
	/// new internal similarity function
	public  double INsimilarity(
			boolean reversed,
			SentenceAlignment a,
			final List<Triples> proposedSentence,
			final List<Triples> referenceSentence, 
			DistanceMatrix d, PrintStream strace) {
		
		new AlignmentBuilderBestMatch().build(reversed,a ,d);
		
	    double res=0;
	    
	    int i_align=0;
	    for(int i_al:a.getAlignment(reversed)) {	
	    	//System.err.println("check "+proposedSentence.size()+" x "+targetSentence.size()+" "+i+":"+al[i]+" sum="+res);
	    	if(i_al>=0) res += d.getDistance(reversed, i_align, i_al);
	    	++i_align;
	    }
	    
	    if(MTsimilarity.DUMP) {
	    	xml_dump_alignment(reversed, a, proposedSentence, referenceSentence, d, strace, i_align);
	    }
	    	
		// we need to normalize
		return res; // al.length >0 ? res / al.length : 0.0;
	}

	private void xml_dump_alignment(boolean reversed, SentenceAlignment a, final List<Triples> proposedSentence,
			final List<Triples> referenceSentence, DistanceMatrix d, PrintStream strace, int i_align) {
		strace.println("<trips n='"+i_align+"' type="+ (reversed?"\"t2s\">" : "\"s2t\">"));
		int i=0;
		for(int i_al:a.getAlignment(reversed)) {	
		strace.println("<trip  tc=\""+tmatch.getWeight(proposedSentence.get(i).label)+"\" sc=\""+(i_al>=0 ? d.getDistance(reversed, i, i_al) : -1)+"\" prov=\""+(i_al>=0 ? a.provenance(reversed,i,i_al) : "nomatch")+"\">");
		strace.println("<src>"+XMLFormater.encodeXMLString(
				(proposedSentence.get(i).toString()))+"</src>");
		if(i_al>=0){ 
			strace.println("<trg>"+XMLFormater.encodeXMLString(referenceSentence.get(i_al).toString())+"</trg>");
		} 
		else {strace.println("<trg>NO MATCH</trg>");}
		strace.println("</trip>");
		i++;
		}
		strace.println("</trips>");
	}
	
	protected DistanceMatrix createDist(boolean reversed, final List<Triples> ts1, final  List<Triples> ts2, final SentenceAlignment dist) {
		DistanceMatrix res = new DistanceMatrix (ts1.size(), ts2.size());
		
		int i=0;
		 for(Triples n1: ts1){
			 int j=0;
			 for(Triples n2: ts2) {
				 if(reversed) {
					 MatchResult tres = tmatch.match(true,n2, n1, dist);
					 res.setDistance(true,j,i,tres.score,tres.prov);}
				 else {
					 MatchResult tres =tmatch.match(false,n1, n2, dist);
				 	res.setDistance(false, i, j,tres.score,tres.prov);	}
				 ++j;
			 }		
			 ++i;
		 }
		return res;
	}

	@Override
	public void dump(PrintStream strace) {
		strace.print("<metric name='dependency triples'/>");
	}
}
