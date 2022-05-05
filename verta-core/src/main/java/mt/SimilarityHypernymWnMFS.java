package mt;

import java.util.Stack;
import edu.smu.tspell.wordnet.SynsetType;
import mt.core.Similarity;
import mt.nlp.Word;
import verta.wn.JABSynset;

public class SimilarityHypernymWnMFS extends SimilarityHypernymWn implements  Similarity {

	public SimilarityHypernymWnMFS(String multilevel) {
		super(multilevel);
	}
	
	public JABSynset[] getMFS(String word_form, SynsetType pos) {
		JABSynset[] res = wn.getSynsets(word_form, pos);
		int lsize = res.length>0 ? 1 : 0;
		JABSynset[] lmax = new JABSynset[lsize];
		int max=-1; int nsense=1; int maxs=0;
		for(JABSynset c: res) {
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
		if(featureReference.equals(featureProposed)) return Similarity.MAXVAL;

		for(SynsetType pos:lpos) {

			JABSynset[] referenceSynsets = getMFS(featureReference, pos);
		
			boolean found=false;
			Stack<JABSynset> pending = new Stack<JABSynset>();
			JABSynset[] proposedSynsets = getMFS(featureProposed, pos);
			
			for(JABSynset s: proposedSynsets) pending.add(s);

			while(!found && ! pending.isEmpty()) {
				JABSynset n = pending.pop();
				JABSynset hypos[] = n.getHypernyms();
				if(hypos!=null) {
					found=SimilarityHypernymWnMFS.nsearchLists(referenceSynsets, hypos);
					if(MULTILEVEL ) for(JABSynset s:hypos) pending.add(s);
				}
			}
			if(found) return Similarity.MAXVAL;
		}
		return Similarity.MINVAL;
	}
	

	static boolean nsearchLists(JABSynset[] referenceSynsets, JABSynset[] hypos) {
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
