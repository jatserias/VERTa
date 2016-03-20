package mt;

public class SimilaritySubstring extends BaseSimilarity implements Similarity {
		public int nchars=1;

		public SimilaritySubstring(String snchars){	
			nchars = Integer.parseInt(snchars);
		}
	/*	
		public SimilaritySubstring(int nchars){
			
			this.nchars = nchars;			
		}
		
		public  SimilaritySubstring(){nchars=1;}
*/
		//@Override
		public double similarity(String[] featureNames ,Word proposedWord, Word referenceWord) {
			boolean hold=true;
			int i=0;
			
			while(i<featureNames.length && hold) {
				String featureName=featureNames[i];
				String proposedValue=proposedWord.getFeature(featureName);
			    String referenceValue = referenceWord.getFeature(featureName);
			    hold = (referenceValue.substring(0,Math.min(nchars, referenceValue.length())).compareTo(proposedValue.substring(0,Math.min(nchars, proposedValue.length())))==0); 
		        ++i;
			}
			return hold ? MAXVAL : MINVAL;
		}

		public String getClassName() {
			return SimilaritySubstring.class.getName();
		}
		public void setReversed(boolean reversed) {}
	}

