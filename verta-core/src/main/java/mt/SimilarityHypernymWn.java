package mt;

import java.util.Stack;

import edu.smu.tspell.wordnet.SynsetType;
import mt.core.Similarity;
import mt.core.WnBaseSimilarity;
import mt.nlp.Word;
import verta.wn.JABSynset;

public class SimilarityHypernymWn extends WnBaseSimilarity implements Similarity {

	// More than one hyper level
	public boolean MULTILEVEL = false;

	// to reverse the behavior (so target,source hyper == source,target hypo)
	protected boolean reversed = false;

	public SimilarityHypernymWn(String multilevel) {
		MULTILEVEL = (multilevel.compareToIgnoreCase("MULTILEVEL") == 0);
	}

	public double similarity(String[] featureNames, Word proposedWord, Word referenceWord) {

		if (reversed)
			return INNERsimilarity(featureNames, referenceWord, proposedWord, SynsetType.ALL_TYPES);
		else
			return INNERsimilarity(featureNames, proposedWord, referenceWord, SynsetType.ALL_TYPES);
	}

	public double INNERsimilarity(String[] featureNames, Word proposedWord, Word referenceWord, SynsetType[] lpos) {

		String featureProposed = proposedWord.getFeature(featureNames[0]);
		String featureReference = referenceWord.getFeature(featureNames[0]);

		// @TODO we probably need to check the Pos
		if (featureReference.equals(featureProposed))
			return Similarity.MAXVAL;

		boolean found = false;
		for (SynsetType pos : lpos) {
			
			JABSynset[] referenceSynsets = wn.getSynsets(featureReference, pos);
			JABSynset[] proposedSynsets = wn.getSynsets(featureProposed, pos);

			Stack<JABSynset> pending = new Stack<JABSynset>();
			// @TODO To use MFS proposedSynsets[0].getTagCount("word form");

			for (JABSynset s : proposedSynsets)
				pending.add(s);

			while (!found && !pending.isEmpty()) {
				JABSynset n = pending.pop();
				JABSynset hypos[] = n.getHypernyms();
				found = searchLists(referenceSynsets, hypos);
				if (MULTILEVEL)
					for (JABSynset s : hypos)
						pending.add(s);
			}
			if (found)
				return Similarity.MAXVAL;
		}
		return Similarity.MINVAL;
	}

	private boolean searchLists(JABSynset[] referenceSynsets, JABSynset[] hypos) {

		if (hypos == null)
			return false;
		int i = 0;
		boolean found = false;
		while (!found && i < hypos.length) {
			int j = 0;
			while (!found && j < referenceSynsets.length) {
				found = (hypos[i].equals(referenceSynsets[j]));
				++j;
			}
			++i;
		}

		return found;
	}

	public String getClassName() {
		return getClassName(this.getClass().getName());
	}

	private String getClassName(String classname) {
		return classname + "." + (MULTILEVEL ? "MULTILEVEL" : "DIRECT");
	}

	public void setReversed(boolean reversed) {
		this.reversed = reversed;

	}

}
