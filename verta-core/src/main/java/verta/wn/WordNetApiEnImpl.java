package verta.wn;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

import java.util.logging.Logger;

public class WordNetApiEnImpl implements WordNetAPI {
	private static Logger LOGGER = Logger.getLogger(WordNetApiEnImpl.class.getName());

	static WordNetDatabase wn;

	public WordNetApiEnImpl() {
		wn = WordNetDatabase.getFileInstance();
		LOGGER.info("Wordnet EN loaded! " + wn);
	}

	public static JABSynset[] convert(Synset[] ns) {
		JABSynset[] res = new JABSynset[ns.length];
		int i = 0;
		byte b;
		int j;
		Synset[] arrayOfSynset;
		for (j = (arrayOfSynset = ns).length, b = 0; b < j;) {
			Synset ins = arrayOfSynset[b];
			res[i++] = new ENSynset(ins);
			b++;
		}
		return res;
	}

	public JABSynset[] getSynsets(String WordForm) {
		return convert(wn.getSynsets(WordForm));
	}

	public JABSynset[] getSynsets(String WordForm, SynsetType pos) {
		return convert(wn.getSynsets(WordForm, pos));
	}

	public String[] getBaseFormCandidates(String wordForm, SynsetType pos) {
		return wn.getBaseFormCandidates(wordForm, pos);
	}

	static SynsetType[] _SN = new SynsetType[] { SynsetType.NOUN };

	static SynsetType[] _SV = new SynsetType[] { SynsetType.VERB };

	static SynsetType[] _SA = new SynsetType[] { SynsetType.ADJECTIVE, SynsetType.ADJECTIVE_SATELLITE };

	static SynsetType[] _SR = new SynsetType[] { SynsetType.ADVERB };
	
	/**
	 * maps pos English into synset types
	 * 
	 * This is language / POS dependent
	 * @param pos
	 * @return
	 */
	@Override
	public SynsetType[] getSynsetTypeFromPos(String pos) {
		switch (pos.charAt(0)) {
		case 'N':
		case 'n':
			return _SN;
		case 'V':
		case 'v':
			return _SV;
		case 'J':
		case 'j':
			return _SA;
		case 'R':
		case 'r':
			return _SR;
		}
		// TODO through exception
		return null;
	}
}
