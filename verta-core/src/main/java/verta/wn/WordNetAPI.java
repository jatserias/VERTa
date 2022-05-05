package verta.wn;

import edu.smu.tspell.wordnet.SynsetType;

/**
 * 
 * This is class is a partial API for the class WordNetDatabase See http://lyle.smu.edu/~tspell/jaws/doc/edu/smu/tspell/wordnet/WordNetDatabase.html.html
 * 
 * Synset is an JAWS interface http://lyle.smu.edu/~tspell/jaws/doc/edu/smu/tspell/wordnet/Synset.html
 * 
 * NounSynset is a JAWS interface http://lyle.smu.edu/~tspell/jaws/doc/edu/smu/tspell/wordnet/NounSynset.html 
 * 
 * Currently:
 * 
 *  wn.getSynsets(word,SynsetType.NOUN) must return noun synsets
 *  NounSynset is used for hypernym/hyponym relations (function getHypernyms()  getHyponyms() )
 *  Synset is used for synonyms (equals function must be implemented)
 *  
 *
 */
public interface WordNetAPI {

	/// Get a list of Synsets from WordForm
	JABSynset[] getSynsets(String WordForm);

	/// Get a list of Synsets from WordForm (SynsetType - PoS)
	JABSynset[] getSynsets(String WordForm, SynsetType pos);

	/// Get all possible lemmas from a  wordForm + PoS
	String[] getBaseFormCandidates(String wordForm, SynsetType pos);

	/// Get all SynsetTypes given a Pos
	SynsetType[] getSynsetTypeFromPos(String pos);
}
