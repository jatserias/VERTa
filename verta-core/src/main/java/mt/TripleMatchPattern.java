package mt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;


/**
 * 
 * new support for
 * 
 * Defining sets of labels (with associate weights)
 * 
 * eg Set1={} with weight X  Set2 with weight={} ...
 * 
 * Define patterns of matchings of triples (pattens to be applied in order)
 * 
 * =: same vale
 * s<NUM>: label on set
 * *: any
 * 
 * (S,H,T)
 * 
 * COMPLETE_WEIGHT       PARTIAL_NOMOD_WEIGHT      PARTIAL_NOHEAD_WEIGHT      PARTIAL_NOLABEL_WEIGHT 
 * 1.0     0.8     0.7     0.7
 * 
 * e.g. (*,S1,*) : NUMBER
 * 
 *  (=,S1,=) : Number
 *  
 *  
 *  Extension to simplify preposition
 *  
 *  deplabel_%
 *  
 * @author jordi
 *
 */
public class TripleMatchPattern extends TriplesMatch {
	
	private static final String COMMENT = "#";

	///Groups of labels with associated weights. A label can be a pattern: dep_%
    HashMap<String,LabelSet> groups;
    
    ///default weight
    double DEFAULT_WEIGHT=0.5;
    
	enum OPERATOR { X, O }
		
	/// a colelction of pattens to try
	Collection<Tpattern> lp;
	
	private void readSets(BufferedReader fconf) throws IOException{
		String buff;
		while((buff=fconf.readLine())!=null  && buff.startsWith("##%SETS"));
		while((buff=fconf.readLine())!=null  && !buff.startsWith("%%#PATTERNS")) {
			if(! buff.startsWith(COMMENT)) {
			LabelSet s = new LabelSet(buff);
			groups.put(s.id,s);
			}
		}
		
		//for(LabelSet l:groups.values()) l.dump(System.err);
	}
	
	
//	private void readEquivalentLabels(BufferedReader fconf) throws IOException{
//		/// Read rules
//		String buff;
//	      while((buff=fconf.readLine())!=null && !buff.startsWith("^%%#SETS")) {
//	    	  if(!buff.trim().startsWith("#")) {
//	    		  String[] label = buff.split("[\t]");
//	    		  labelMatch.put(label[0]+"#"+label[1], Double.parseDouble(label[2]));
//	    	  }
//	      }
//	}
	
	private void readPatterns(BufferedReader fconf) throws IOException{
		String buff;
		while((buff=fconf.readLine())!=null) {
			if(!buff.startsWith(COMMENT)) {
			if(buff.trim().length()>0) {
				Tpattern s = new Tpattern(buff,this.groups);
				lp.add(s);
			 }
			}
		}
		//System.err.println("PATTERNS");
		//for(Tpattern s:lp) s.dump(System.err);
		//System.exit(-1);
	}
	public TripleMatchPattern(String filename,MetricActivationCounter counters) throws IOException {
		super(counters);
		System.err.println("Initilizing patterns");
		// TODO load the rest of parameters
		groups = new HashMap<String,LabelSet>();
		lp = new ArrayList<Tpattern>();
		System.err.println("Loading "+filename.substring(4));
		BufferedReader fconf=  (filename.startsWith("jar:")  ? 
				    new BufferedReader(new InputStreamReader(TripleMatchPattern.class.getResourceAsStream(filename.substring(4)))) :
					new BufferedReader(new FileReader(filename))
				    );
		
		readSets(fconf);
		readPatterns(fconf);
		dump(System.err);
	}

	private void dump(PrintStream err) {
		err.println("label set");
		 for(Entry<String, LabelSet> e :groups.entrySet()){
			 err.println(e.getKey()+"=>"+e.getValue());
		 }
		 
		 err.println("patterns");
		 for(Tpattern p:lp) {
		    	p.dump(err);
		 }
	}


	@Override
	public MatchResult matchingScorer(final Triples x, final Triples y, boolean label_match, boolean source_match, boolean  target_match) {
		// we should combine weight
		// Pw * Lw * Sw
		
		//System.err.println("DEP: "+x+" vs "+y);
	    for(Tpattern p:lp) {
	    	//p.dump(System.err);
	    	if(p.match(x,y,label_match,source_match,target_match)) {
	    		//System.err.println("MATCH p.weight:"+p.weight);
	    		//System.err.print("MATCH label");
	    		//if(p.labelSet==null) System.err.println("SET:"+getWeight(x.label)); else System.err.println(": "+p.labelSet.w);
	    		counters.increase(this.getClass().getName()+"["+p.toString()+"]",1,false);
	    		return new MatchResult( p.weight  * (p.labelSet==null ? getWeight(x.label) :p.labelSet.w), p);
	    		
	    	}
	    	//else {System.err.println("FAIL");}
	    }
	    // Should we return 0 or -1 ??
	    return TriplesMatch.NOMATCH;
		
	}
	
	/**
	 * getting the weigh of a label or set of labels
	 */
	public double getWeight(String label) {
		for(LabelSet l :groups.values()) {
			if(l.contains(label.toLowerCase())) return l.w;
		}
		// try extended pattern
		int pos = label.indexOf('_');
		if(pos>0) {
			String mlabel = label.substring(0,pos)+"_%";
		for(LabelSet l :groups.values()) {
			if(l.contains(mlabel.toLowerCase())) return l.w;
		}
		}
		
		//TODO Return 1.0 for unknown labels 
		return 1.0;
	}


	public boolean labelInGroup(String group,String label) {		 
		  LabelSet g = groups.get(group);
		  return g.labels.contains(label);
		  
	  }
	
	//TODO turn into junit
	public static void main(String args[]) throws IOException {
		TripleMatchPattern t= new TripleMatchPattern("conf/triplesmatch.conf", new MetricActivationCounter());
		boolean reversed=false;
		Triples  x = new Triples("amod_de", 1, 2);
		Triples  y = new Triples("prep_of", 1, 2);
		SentenceAlignment align = new AlignmentImpl(2,2);
		align.setAligned(reversed, 0,0,"");
		align.setAligned(reversed, 1,1,"");
		System.err.println(x+" x "+ y+"\nMATCH:"+t.match(reversed, x, y, align));
		y = new Triples("prep_oxx", 1, 2);
		System.err.println(x+" x "+ y+"\nMATCH:"+t.match(reversed, x, y, align));
		x = new Triples("sbj_by", 1, 2);
		y = new Triples("sbj", 1, 2);
		System.err.println(x+" x "+ y+"\nMATCH X X X sbj:"+t.match(reversed, x, y, align));
		System.err.println(t.getWeight("amod_de"));
	}
}
