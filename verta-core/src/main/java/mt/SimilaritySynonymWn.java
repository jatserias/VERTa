package mt;

/**
 * 
 * WordNet synonms
 * 
 *  1 if there is a common synonym
 *  0 otherwise
 * 
 * @author jordi
 *
 */
public class SimilaritySynonymWn extends WnBaseSimilarity implements Similarity {

	
	public double similarity(String[] featureNames, Word proposedWord,
			Word referenceWord) {
		
		String featureProposed = proposedWord.getFeature(featureNames[0]);
		String featureReference = referenceWord.getFeature(featureNames[0]);
		
		if(featureReference.equals(featureProposed)) return Similarity.MAXVAL;
			
		JABSynset[] proposedSynsets=wn.getSynsets(featureProposed);
		JABSynset[] referenceSynsets = wn.getSynsets(featureReference);
	
		//System.err.println("HERE "+proposedSynsets.length+":"+referenceSynsets.length);
		return similarity(featureProposed, featureReference, proposedSynsets, referenceSynsets);	

	}
		
		public double similarity(String featureProposed, String featureReference, final JABSynset[] proposedSynsets, final JABSynset[] referenceSynsets) {	
		int i=0;boolean found=false;
		while(!found && i< proposedSynsets.length) {
			int j=0;
			while(!found && j < referenceSynsets.length) {
				//System.err.println(proposedSynsets[i]+"<>"+referenceSynsets[j]);
				found =(proposedSynsets[i].equals(referenceSynsets[j]));
				++j;
			}
			++i;	
		}
		
		//@JAB TRACE
		/**System.err.print( "WNsym("+ featureProposed +","+featureReference+")=syn:");
		if(found) {
			System.err.println(proposedSynsets[i-1].getDefinition());
		}
		else {
			System.err.println("0");
		}
		**/
		return found ? Similarity.MAXVAL : Similarity.MINVAL;
	}

	public String getClassName() {
		return SimilaritySynonymWn.class.getName();
	}

	public void setReversed(boolean reversed) {}
}
