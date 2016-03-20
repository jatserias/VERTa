package mt;


import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class WordNetApiEnImpl implements WordNetAPI{
	WordNetDatabase wn = WordNetDatabase.getFileInstance();
	
	
	public static JABSynset[] convert(Synset[] ns) {
		JABSynset res[] = new JABSynset[ns.length];
		   int i=0;
		   for(Synset ins:ns){
			   res[i++]=new ENSynset(ins);
		   }
		   return res;
	}
	                 
	@Override
	public JABSynset[] getSynsets(String WordForm) {
		return convert(wn.getSynsets(WordForm));
	}

	
	public JABSynset[] getSynsets(String WordForm, SynsetType pos) {
		return convert(wn.getSynsets(WordForm, pos));
	}

	
	public String[] getBaseFormCandidates(String wordForm, SynsetType pos) {
		return wn.getBaseFormCandidates(wordForm, pos);
	}

	
	static SynsetType[] _SN = {SynsetType.NOUN};
	static SynsetType[] _SV = {SynsetType.VERB};
	static SynsetType[] _SA = {SynsetType.ADJECTIVE,SynsetType.ADJECTIVE_SATELLITE};
	static SynsetType[] _SR = {SynsetType.ADVERB};
	
	/**
	 * maps pos English into synset types
	 * 
	 * This is language / POS dependent
	 * @param pos
	 * @return
	 */
	@Override
	public SynsetType[] getSynsetTypeFromPos(String pos) {
		switch(pos.charAt(0))  {
		case 'N':
		case 'n': return _SN;
	
		case 'V':
		case 'v':
			return _SV;
			
		case 'J':
		case 'j':
			return _SA;
			
		case 'R':
		case 'r':
			return _SR;
		default:
			return null;
			
		}
		
	}
	
	
}
