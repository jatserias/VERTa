package mt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/*
 * A class for MAtching triples
 */
public class TriplesMatch  {
private static final String LABEL_PAIR_SEPARATOR = "#";
	// we need to read this weigh from the config file
	private static  MatchResult COMPLETE_WEIGHT = new MatchResult(1.0,"(x,x,x)");
	private static  MatchResult PARTIAL_NOMOD_WEIGHT = new MatchResult(0.8,"(x,x,o)");
	private static MatchResult PARTIAL_NOHEAD_WEIGHT = new MatchResult(0.7,"(x,o,x)");
	private static  MatchResult PARTIAL_NOLABEL_WEIGHT = new MatchResult(0.7,"(o,x,x)");
	public static  MatchResult NOMATCH = new MatchResult(0.0,"(o,o,o)");
	
	
   ///	label # label matching table
   HashMap<String, Double> labelMatch;
	
   // we also need to incorporate equivalent  labels
    // label label weight
	
   // to report counters
   MetricActivationCounter counters;
   
   public TriplesMatch(MetricActivationCounter counters) {
		labelMatch= new HashMap<String, Double>();
		this.counters=counters;
   }
   
   /**
    * read and old file dep configuration
    * 
    * @param filename
    */
	public TriplesMatch(String filename,MetricActivationCounter counters) {
		this.counters=counters;
		labelMatch= new HashMap<String, Double>();
		
	 BufferedReader config=null;
	try {
		  config = new BufferedReader(new FileReader(filename));
		 }
		 catch(Exception e) {
			System.err.println("ERROR can not open/find file >"+filename+"<");
			e.printStackTrace();
			System.exit(-1);
		 }
	      String buff=null;
	      try {
	    	 
	      // read weights
	      
		while((buff=config.readLine())!=null && buff.trim().startsWith(LABEL_PAIR_SEPARATOR));
	      if(buff==null)  {
	    	  System.err.println("Format ERROR on the triple config file >"+filename+"<");
	          System.err.println("EMPTY FILE");
		      System.exit(-1);
	      }
	      
	      String[] wbuff = buff.split("[\t]");
	      int i=0;
	        COMPLETE_WEIGHT.score = Double.parseDouble(wbuff[i++]);
	  	    PARTIAL_NOMOD_WEIGHT.score = Double.parseDouble(wbuff[i++]);
	  	    PARTIAL_NOHEAD_WEIGHT.score = Double.parseDouble(wbuff[i++]);
	  	    PARTIAL_NOLABEL_WEIGHT.score = Double.parseDouble(wbuff[i++]);
	     
	      /// Read rules
	      while((buff=config.readLine())!=null) {
	    	  if(!buff.trim().startsWith(LABEL_PAIR_SEPARATOR)) {
	    		  String[] label = buff.split("[\t]");
	    		  labelMatch.put(label[0]+LABEL_PAIR_SEPARATOR+label[1], Double.parseDouble(label[2]));
	    	  }
	      }
	      }catch(Exception e) {
	    	  System.err.println("Error reading triplet config file:"+buff);
	    	  e.printStackTrace();
	      }
	}
	
	public double getWeight(String label) {return 1.0;}
	
	/**
	 * return the score given the matching pattern
	 * 
	 * @param label_match
	 * @param head_match
	 * @param mod_match
	 * @return
	 */
	
	
	public MatchResult  matchingScorer(final Triples x, final Triples y, boolean label_match, boolean head_match, boolean  mod_match) {
		// This is the part to be customized
				if(label_match && head_match && mod_match) return COMPLETE_WEIGHT;
				if(label_match && head_match) return PARTIAL_NOMOD_WEIGHT;
				if(label_match && mod_match) return PARTIAL_NOHEAD_WEIGHT;
				if(head_match && mod_match) return PARTIAL_NOLABEL_WEIGHT;
				
				return NOMATCH;
	}
	
	
	@Deprecated
	public MatchResult match(boolean reversed, final Triples x, final Triples y, final SentenceAlignment align) {
		
		boolean label_match = labelsMatch(x, y);
		
		
		// JORDI bug head - mod. We reverse the matchings
		boolean mod_match = align.isAligned(reversed,x.getTarget()-1,y.getTarget()-1);
		
		boolean head_match = 
			(x.getSource()>0 && y.getSource()>0) ?			
					 align.isAligned(reversed,x.getSource()-1,y.getSource()-1) :
					(x.getSource()==y.getSource()); // root 
		//if((x.target>0) && (y.target>0)) {
		//	System.err.println(align[y.target-1][x.target-1]);
		//	System.err.println(align[x.target-1][y.target-1]);
	//	}
		//System.err.println("label match:"+label_match);
		//System.err.println("source match:"+source_match);
		//System.err.println("target match:"+target_match);
		
		
		// how we should apply label matching rules
		label_match = label_match || matchRules(x.label,y.label);
					
		
		
		
		return matchingScorer(x,y,label_match,head_match, mod_match);
	}

   public boolean labelsMatch(final Triples x, final Triples y) {
	    String elabelx=getSubLabel(x.label);
		String elabely=getSubLabel(y.label);
		//System.err.println("TRACE:"+elabelx+" "+elabely+" "+isPatternLabel(x.label)+" "+isPatternLabel(y.label));
		return (x.label.compareTo(y.label)==0) ||
				(
				  (isPatternLabel(x.label) || isPatternLabel(y.label)) 
				       && 
				  (elabelx.compareTo(elabely)==0)
			  );
	}

   /**
    * 
    * TODO this function seems to be call but labelMatch is empty (for the new dep match version)
    * @param label
    * @param label2
    * @return
    */
	public boolean matchRules(String label, String label2) {
		// extended
		String elabel=getSubLabel(label);
		String elabel2=getSubLabel(label2);
		//System.err.println("TRACE:"+elabel+" "+elabel2);
		//System.err.println(labelMatch);
		return
				// direct match
			  (labelMatch.get(label+LABEL_PAIR_SEPARATOR+label2)!=null || labelMatch.get(label2+LABEL_PAIR_SEPARATOR+label)!=null ) || 
			    // pattern label match
		      ( (isPatternLabel(label) || isPatternLabel(label2) )  
		    		  && 
		       (labelMatch.get(elabel+LABEL_PAIR_SEPARATOR+elabel2)!=null || labelMatch.get(elabel2+LABEL_PAIR_SEPARATOR+elabel)!=null ));
	}

	static public String getSubLabel(String label) {
		int pos= label.indexOf('_');
		return pos>0 ? label.substring(0,pos) : label;
	}

	static	public boolean  isPatternLabel(String label) {
		return label.endsWith("_%");
	}

	//TODO conert to junit
	@Deprecated
	public static void main(String args[]) throws IOException  {
		assert(TriplesMatch.isPatternLabel("dep_%)"));
		assert(!TriplesMatch.isPatternLabel("dep%)"));
		assert(!TriplesMatch.isPatternLabel("dep_a)"));
		assert(TriplesMatch.getSubLabel("dep_a)").equals("a"));
		assert(TriplesMatch.getSubLabel("dep_de)").equals("de"));
		assert(TriplesMatch.getSubLabel("subj").equals("subj"));
		TriplesMatch tm = new TriplesMatch(new MetricActivationCounter());
		tm.labelMatch.put("dep_b#dep_a",2.0);
		assert(tm.matchRules("dep_%", "dep_a"));
		assert(tm.matchRules("dep_a", "dep_%"));
		assert(tm.labelsMatch(new Triples("dep_%",0,0), new Triples("dep_a",0,0)));
		assert(tm.labelsMatch(new Triples("dep_a",0,0), new Triples("dep_%",0,0)));
		assert(!tm.labelsMatch(new Triples("kk_a",0,0), new Triples("dep_%",0,0)));
		assert(!tm.labelsMatch(new Triples("dep_a",0,0), new Triples("kk_%",0,0)));
		System.out.println(tm.matchingScorer(new Triples("dep_%",0,0), new Triples("dep_a",0,0),true,true,true));
		System.out.println(tm.matchingScorer(new Triples("dep_b",0,0), new Triples("dep_a",0,0),true,true,true));
	}
}
