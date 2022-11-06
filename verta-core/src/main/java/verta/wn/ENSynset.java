package verta.wn;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;

public class ENSynset implements ISynset {

	public Synset s;
	
	public ENSynset(Synset ns) {
		s=ns;
	}

	@Override
	public ISynset[] getHypernyms() {
	   SynsetType t = s.getType();
	   if(t==SynsetType.NOUN) return WordNetEnImpl.convert(((NounSynset) s).getHypernyms());
	   if(t==SynsetType.VERB) return WordNetEnImpl.convert(((VerbSynset) s).getHypernyms());
	   return null;
	}

	@Override
	public String[] getWordForms() {
		return s.getWordForms();
	}

	@Override
	public int getTagCount(String wf) {
		return s.getTagCount(wf);
	}

	public String toString(){
		return s.toString()+"\n";
	}
	
	@Override 
	public boolean equals(Object o) {
		   
	    if ( this == o ) return true;

	    if ( !(o instanceof ENSynset) ) return false;
	   //cast to native object is now safe
	    ENSynset eno = (ENSynset) o;

	    //now a proper field-by-field evaluation can be made
	    return eno.s.equals(this.s);
	}
}
