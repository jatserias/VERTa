package mt;

import java.util.Stack;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;

public class SimilarityHypernymWnMFS extends SimilarityHypernymWn implements  Similarity {

	public SimilarityHypernymWnMFS(String multilevel) {
		super(multilevel);
	}

//	//TODO DEfault POS noun
//	public JABSynset[] getMFS(String featureReference) {
//		return getMFS(featureReference,SynsetType.NOUN);
//	}
	
	public JABSynset[] getMFS(String featureReference, SynsetType pos) {
		JABSynset[] res = wn.getSynsets(featureReference, pos);
		int lsize = res.length>0 ? 1 : 0;
		JABSynset[] lmax = new JABSynset[lsize];
		int max=-1; int nsense=1; int maxs=0;
		for(JABSynset c: res) {
			int nmax =0;
			for(String wf: c.getWordForms()) {
		    	nmax+=c.getTagCount(wf);
		    }		
			//System.err.println("#"+nmax+" sense:"+ c.toString());
			if(max<nmax) { lmax[0]=c; max=nmax; maxs=nsense;}
			nsense++;
		}
		//if(lsize>0) System.err.println("Returning "+maxs+" sense:"+ lmax[0].toString()+" "+ (lmax[0].getDefinition()));
		return lmax;
	}
	
	@Override
	public double INNERsimilarity(String[] featureNames, Word proposedWord, Word referenceWord, SynsetType[] lpos) {

	// ?? Filter out by assigned PoS	
	//	if(!(proposedWord.getFeature(FT_POS).toLowerCase().startsWith("n") &&
	//	   referenceWord.getFeature(FT_POS).toLowerCase().startsWith("n"))) return 0;
			
		String featureProposed = proposedWord.getFeature(featureNames[0]);
		String featureReference = referenceWord.getFeature(featureNames[0]);		
		
		//@TODO we probably need to check the Pos
		if(featureReference.equals(featureProposed)) return Similarity.MAXVAL;
		
		for(SynsetType pos:lpos) {
			
		JABSynset[] referenceSynsets = getMFS(featureReference, pos);
		
		boolean found=false;
		Stack<JABSynset> pending = new Stack<JABSynset>();
		JABSynset[] proposedSynsets=getMFS(featureProposed,pos);
		for(JABSynset s: proposedSynsets) pending.add(s);

		while(!found && ! pending.isEmpty()) {
			JABSynset n=  pending.pop();
			JABSynset hypos[] = n.getHypernyms();
			if(hypos!=null) {
				found=nsearchLists(referenceSynsets,hypos);
				if(MULTILEVEL ) for(JABSynset s:hypos) pending.add(s);
			}
		}
		 if(found) return Similarity.MAXVAL;
		}
		return Similarity.MINVAL;
	}
	

	boolean nsearchLists(JABSynset[] referenceSynsets, JABSynset[] hypos) {
		int i=0;boolean found=false;
		while(!found && i< hypos.length) {
			int j=0;
			while(!found && j < referenceSynsets.length) {
				found =(hypos[i].equals(referenceSynsets[j]));
				++j;
			}
			++i;	
		}
		//if(found) {
		//	System.err.println("**** MFS Hyper found:"+hypos[i-1].toString()+"\n"+hypos[i-1].getDefinition());
	//	}
		return found;
	}
}
