package mt.nlp;

/// Seems to be only used byt the old matching triples
public class Triples {
	String sourceString;
	String targetString;

	/// mod
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
	
	
	

	public static final String DEPLABEL_NAME = "DEPLABEL";

	public static final String ID_NAME = "ID";

	public static final String WORD_NAME = "WORD";

	public static final String DEPHEAD_NAME = "DEPHEAD";
	
	public Triples(final Sentence s1, final Word w) {

		// mod and head were reversed, bug fixed on 15/05/2014
		// 1 Parliament NNP NNP parliament B-ORG B-noun.other
		// B-E:ORGANIZATION:GOVERNMENT nsubj 4
		// source (head) 4
		// target (mod) 1
		target = Integer.parseInt(w.getFeature(ID_NAME));
		targetString = w.getFeature(WORD_NAME);

		String head = w.getFeature(DEPHEAD_NAME);
		source = head.startsWith("_") ? -1 : Integer.parseInt(w.getFeature(DEPHEAD_NAME));
		sourceString = (source < 1) ? "TOP" : s1.get(source - 1).getFeature(WORD_NAME);
		label = w.getFeature(DEPLABEL_NAME);

	}

	public String toString() {
		return label + "(" + sourceString + ":" + source + "," + targetString + ":" + target + ")";
	}

}
