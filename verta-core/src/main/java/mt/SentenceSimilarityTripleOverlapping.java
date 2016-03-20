package mt;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

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


	public static boolean FILTER_TOP =false;
	
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
		tmatch= USE_OLD ? new TriplesMatch(configFile,counters) : new TripleMatchPattern(configFile,counters); 
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
		strace.println("<trip  sc=\""+res+"\">");
		strace.println("<src>"+XMLFormater.encodeXMLString(n1.toString())+"</src>");
		if(btrip!=null){ 
			strace.println("<trg>"+XMLFormater.encodeXMLString(btrip.toString())+"</trg>");
		} 
		else {strace.println("<trg>NO MATCH</trg>");}
		strace.println("</trip>");
		}
		return res;
	}
	
//	private static boolean compareTriple(
//			boolean reversed, 
//			final Triples n1,
//			final Triples n2, 
//			final SentenceAlignment align) {
//		return align.aligned(reversed,n1.source,n2.source) && align.aligned(reversed,n1.target,n2.target) && n1.equals(n2);
//	}
//
	
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
	static private List<Triples> tripleGenerator(final Sentence s1) {
		List<Triples> res = new Vector<Triples>();
		for(Word w: s1) {
			Triples t =  new Triples(s1, w);
			
			if(FILTER_TOP || t.getTarget()>=1) res.add(t);
		}
		return res;
	}

//	private static Triples createTriple(final Sentence s1, Word w) {
//		Triples t = new Triples();
//		
//		// mod and head were reversed, bug fixed on 15/05/2014
//		//1       Parliament      NNP     NNP     parliament      B-ORG   B-noun.other    B-E:ORGANIZATION:GOVERNMENT     nsubj   4
//		// source (head) 4
//		// target (mod)  1
//		t.source=Integer.parseInt(w.getFeature(ID_NAME));
//		t.sourceString=w.getFeature(WORD_NAME);
//		String head =w.getFeature(DEPHEAD_NAME);
//		t.target = head.startsWith("_") ?  -1 :Integer.parseInt(w.getFeature(DEPHEAD_NAME));
//		t.targetString =  (t.target<1) ? "TOP" : s1.get(t.target-1).getFeature(WORD_NAME);
//		t.label=w.getFeature(DEPLABEL_NAME);
//		return t;
//	}

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
		double[] tres = new  double[2];
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
	/**
	 * new internal similarity function
	 * 
	 * @param reversed
	 * @param a
	 * @param proposedSentence
	 * @param referenceSentence
	 * @param d
	 * @param strace
	 * @return
	 */
	public  double INsimilarity(
			boolean reversed,
			SentenceAlignment a,
			final List<Triples> proposedSentence,
			final List<Triples> referenceSentence, 
			DistanceMatrix d, PrintStream strace) {
		
		new AlignmentBuilderBestMatch().build(reversed,a ,d);
		
		int [] al=a.aligned(reversed);
	    double res=0;
	    
	    for(int i=0;i<al.length;++i) {	
	    	//System.err.println("check "+proposedSentence.size()+" x "+targetSentence.size()+" "+i+":"+al[i]+" sum="+res);
	    	if(al[i]>=0) res += d.getDistance(reversed, i, al[i]);
	    }
	    
	    if(MTsimilarity.DUMP) {
	    	strace.println("<trips n='"+al.length+"' type="+ (reversed?"\"t2s\">" : "\"s2t\">"));
	    	for(int i=0;i<al.length;++i) {	
	    	strace.println("<trip  tc=\""+tmatch.getWeight(proposedSentence.get(i).label)+"\" sc=\""+(al[i]>=0 ? d.getDistance(reversed, i, al[i]) : -1)+"\" prov=\""+(al[i]>=0 ? a.provenence(reversed,i,al[i]) : "nomatch")+"\">");
			strace.println("<src>"+XMLFormater.encodeXMLString(
					(proposedSentence.get(i).toString()))+"</src>");
			if(al[i]>=0){ 
				strace.println("<trg>"+XMLFormater.encodeXMLString(referenceSentence.get(al[i]).toString())+"</trg>");
			} 
			else {strace.println("<trg>NO MATCH</trg>");}
			strace.println("</trip>");
	    	}
	    	strace.println("</trips>");
	    }
	    //System.err.println("returning res:"+(al.length >0 ? res / al.length : 0.0)+ " sum:"+res+" num:"+ al.length );
		// we need to normalize
		return res; // al.length >0 ? res / al.length : 0.0;
	}
	
	private DistanceMatrix createDist(boolean reversed, final List<Triples> ts1, final  List<Triples> ts2, final SentenceAlignment dist) {
		DistanceMatrix res = new DistanceMatrix (ts1.size(),ts2.size());
		int i=0;
		 for(Triples n1:ts1){
			 int j=0;
			 for(Triples n2:ts2) {
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

	//@Override
	public  double[] OLDsimilarity(
			final Sentence proposedSentence,
			final Sentence referenceSentence, 
			final SentenceAlignment dist, PrintStream strace) {
		
		double[] tres = new  double[2];
		
		List<Triples> ts1 = tripleGenerator(proposedSentence);		
		List<Triples> ts2 = tripleGenerator(referenceSentence);
		
		
		//for(Triples t:ts1) System.err.println(t.dump());
		if(MTsimilarity.DUMP)  	strace.println("<trips type=\"s2t\">");
		double resp = compare(false, ts1, ts2, dist,strace);
		if(MTsimilarity.DUMP) strace.println("</trips>\n<trips type=\"t2s\">");
		double resr = compare(true, ts2, ts1, dist,strace);
		if(MTsimilarity.DUMP) strace.println("</trips>");
		tres[0] =  ts1.size() >0 ? resp /  ts1.size() : 0;
		tres[1] = ts1.size() >0 ?  resr / ts2.size() : 0;
		return tres;
	}

	@Override
	public void dump(PrintStream strace) {
		strace.print("<metric name='dependency triples'/>");
	}
}
