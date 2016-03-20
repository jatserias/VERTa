package mt;

/**
 * 
 * Seems to be only used byt the old matching triples
 * @author jordi
 *
 */
public class Triples {
 String sourceString;
 String targetString;
 
/// mod
 private int source;
///head 
 private int target;
 
 //label
 public String label;
 
 
public int getSource() {
	return source;
}

public int getTarget() {
	 return target;
}
public Triples(String plabel,int i, int j) {
	source=i;
	target=j;
	label=plabel;
}

//public Triples() {
//	// TODO Auto-generated constructor stub
//}

private static final String DEPLABEL_NAME = "DEPLABEL";

private static final String ID_NAME = "ID";

private static final String WORD_NAME = "WORD";

private static final String DEPHEAD_NAME = "DEPHEAD";

public Triples(final Sentence s1, final Word w) {
	
	// mod and head were reversed, bug fixed on 15/05/2014
	//1       Parliament      NNP     NNP     parliament      B-ORG   B-noun.other    B-E:ORGANIZATION:GOVERNMENT     nsubj   4
	// source (head) 4
	// target (mod)  1
	target=Integer.parseInt(w.getFeature(ID_NAME));
	targetString=w.getFeature(WORD_NAME);
	
	String head =w.getFeature(DEPHEAD_NAME);
	source = head.startsWith("_") ?  -1 :Integer.parseInt(w.getFeature(DEPHEAD_NAME));
	sourceString =  (source<1) ? "TOP" : s1.get(source-1).getFeature(WORD_NAME);
	label=w.getFeature(DEPLABEL_NAME);
	
}
public String toString() {
	return label+"("+sourceString+":"+source+","+targetString+":"+target+")";
}
 

}
