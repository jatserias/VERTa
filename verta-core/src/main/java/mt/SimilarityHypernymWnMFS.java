package mt;

import java.util.Stack;
import edu.smu.tspell.wordnet.SynsetType;
import mt.core.Similarity;
import mt.nlp.Word;
import verta.wn.ISynset;

public class SimilarityHypernymWnMFS extends SimilarityHypernymWn implements  Similarity {

	public SimilarityHypernymWnMFS(String multilevel) {
		super(multilevel);
	}
	
	public ISynset[] getMFS(String word_form, SynsetType pos) {
		ISynset[] res = wn.getSynsets(word_form, pos);
		int lsize = res.length>0 ? 1 : 0;
		ISynset[] lmax = new ISynset[lsize];
		int max=-1; int nsense=1; int maxs=0;
		for(ISynset c: res) {
			int nmax =0;
			for(String wf: c.getWordForms()) {
		    	nmax+=c.getTagCount(wf);
		    }		
			if(max<nmax) { lmax[0]=c; max=nmax; maxs=nsense;}
			nsense++;
		}
		return lmax;
	}
	
	@Override
	public double INNERsimilarity(String[] featureNames, Word proposedWord, Word referenceWord, SynsetType[] lpos) {

		String featureProposed = proposedWord.getFeature(featureNames[0]);
		String featureReference = referenceWord.getFeature(featureNames[0]);		
			
		//@TODO we probably need to check the PoS
		if(featureReference.equals(featureProposed)) return Similarity.MAX_VAL;

		for(SynsetType pos:lpos) {

			ISynset[] referenceSynsets = getMFS(featureReference, pos);
		
			boolean found=false;
			Stack<ISynset> pending = new Stack<ISynset>();
			ISynset[] proposedSynsets = getMFS(featureProposed, pos);
			
			for(ISynset s: proposedSynsets) pending.add(s);

			while(!found && ! pending.isEmpty()) {
				ISynset n = pending.pop();
				ISynset hypos[] = n.getHypernyms();
				if(hypos!=null) {
					found=SimilarityHypernymWnMFS.nsearchLists(referenceSynsets, hypos);
					if(MULTILEVEL ) for(ISynset s:hypos) pending.add(s);
				}
			}
			if(found) return Similarity.MAX_VAL;
		}
		return Similarity.MIN_VAL;
	}
	

	static boolean nsearchLists(ISynset[] referenceSynsets, ISynset[] hypos) {
		int i=0;boolean found=false;
		while(!found && i< hypos.length) {
			int j=0;
			while(!found && j < referenceSynsets.length) {
				found =(hypos[i].equals(referenceSynsets[j]));
				++j;
			}
			++i;	
		}
		return found;
	}
}
