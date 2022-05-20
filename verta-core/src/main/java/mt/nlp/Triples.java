package mt.nlp;

import mt.SentenceSimilarityTripleOverlapping;

/// Seems to be only used byt the old matching triples
public class Triples {
	
	public static final String ID_NAME = "ID";
    public static final String WORD_NAME = "WORD";
	
	
	private String sourceString;
	
	private String targetString;

	/// label
	private int source;
	
	/// head
	private int target;

	// label
	public String label;

	public int getSource() {
		return source;
	}

	public int getTarget() {
		return target;
	}

	public Triples(String label, int source, int target) {
		this.source = source;
		this.target = target;
		this.label = label;
	}
	
	public Triples(String label, int source, int target, String sourceString, String targetString) {
		this.source = source;
		this.target = target;
		this.label = label;
		this.sourceString = sourceString; 
		this.targetString = targetString;
	}
	
	@Override
    public boolean equals(Object obj)
    {
          
		if(this == obj) return true;
          
        if(obj == null || obj.getClass()!= this.getClass())
            return false;
          
        Triples triple = (Triples) obj;
          
        return (triple.source == this.source && triple.target == this.target && triple.label.equals(this.label));
    }
	
	public int hashCode() {
		return this.toString().hashCode();
	}
	

	public String toString() {
		return label + "(" + sourceString + ":" + source + "," + targetString + ":" + target + ")";
	}

}
