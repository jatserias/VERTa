package junit;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class WordNetTest {
	public static void main(String[] args) throws Exception {
		NounSynset nounSynset;
		NounSynset[] hyponyms;

		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] synsets = database.getSynsets("fly", SynsetType.NOUN);
		for (int i = 0; i < synsets.length; i++) {
		    nounSynset = (NounSynset)(synsets[i]);
		   System.err.println("synset:"+nounSynset);
		    for(String wf: nounSynset.getWordForms()) {
		    	System.err.println("wf:"+wf+ " fr:"+nounSynset.getTagCount(wf));
		    }
		    hyponyms = nounSynset.getHypernyms();
		   // System.err.println(nounSynset.getWordForms()[0] +	            ": " + nounSynset.getDefinition() + ") has " + hyponyms.length + " hyponyms");
		} 
		
		
	}
}
