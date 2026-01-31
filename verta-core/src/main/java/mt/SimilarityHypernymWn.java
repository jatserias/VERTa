package mt;

import java.util.Stack;

import edu.smu.tspell.wordnet.SynsetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import mt.core.IFeaturesWordSimilarity;
import mt.core.WnBaseSimilarity;
import mt.nlp.Word;
import verta.wn.ISynset;
import verta.wn.IWordNet;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class SimilarityHypernymWn extends WnBaseSimilarity {

	// More than one hyper level
	@Builder.Default
	public boolean multilevel = false;

	// to reverse the behavior (so target,source hyper == source,target hypo)
	@Builder.Default
	protected boolean reversed = false;

	public SimilarityHypernymWn(IWordNet wn, String multilevel) {
		super(wn);
		this.multilevel = (multilevel.compareToIgnoreCase("MULTILEVEL") == 0);
	}

	public SimilarityHypernymWn(String multilevel) {
		super();
		this.multilevel = (multilevel.compareToIgnoreCase("MULTILEVEL") == 0);
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
			return IFeaturesWordSimilarity.MAX_VAL;

		boolean found = false;
		for (SynsetType pos : lpos) {
			
			ISynset[] referenceSynsets = wn.getSynsets(featureReference, pos);
			ISynset[] proposedSynsets = wn.getSynsets(featureProposed, pos);

			log.warn("references");
			for(ISynset synset: referenceSynsets)
				log.warn(synset.toString());
			
			log.warn("proposed");
			for(ISynset synset: proposedSynsets)
				log.warn(synset.toString());

			Stack<ISynset> pending = new Stack<ISynset>();
			// @TODO To use MFS proposedSynsets[0].getTagCount("word form");

			for (ISynset s : proposedSynsets) pending.add(s);
			
			log.warn("pending");
			for(ISynset synset: pending)
				log.warn(synset.toString());

			while (!found && !pending.isEmpty()) {
				log.warn("loop");
				ISynset n = pending.pop();
				ISynset hypos[] = n.getHypernyms();
				log.warn(String.format("hyper %d",hypos.length));
				found = searchLists(referenceSynsets, hypos);
				if (multilevel)
					for (ISynset s : hypos)
						pending.add(s);
			}
			if (found)
				return IFeaturesWordSimilarity.MAX_VAL;
		}
		return IFeaturesWordSimilarity.MIN_VAL;
	}

	private boolean searchLists(ISynset[] referenceSynsets, ISynset[] hypos) {

		if (hypos == null)
			return false;
		int i = 0;
		boolean found = false;
		while (!found && i < hypos.length) {
			int j = 0;
			while (!found && j < referenceSynsets.length) {
				log.warn(String.format("comparing %s with %s", hypos[i].toString(), referenceSynsets[j].toString()));
				found = (hypos[i].equals(referenceSynsets[j]));
				++j;
			}
			++i;
		}

		return found;
	}

	public String toString() {
		return this.getClass().getName() + "." + (multilevel ? "MULTILEVEL" : "DIRECT");
	}

}
