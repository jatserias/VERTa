package mt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

/**
 * 
 * similarity using a confusion matrix
 * @author jordi
 *
 */
public class SimilarityConfusionMatrix extends BaseSimilarity implements Similarity  {
	private static final String JOOIN_STRING = "+";
	double defaultValue;
    HashMap<String,Double> confusionMatrix;
    double maxweight=0;
    
    public SimilarityConfusionMatrix(String filename) {
    	defaultValue=0;
    	confusionMatrix = new HashMap<String,Double>();
    	
    	// read confusion matrix from filename
    	
    	
    	try {
    	BufferedReader in = new BufferedReader(new FileReader(filename));
    	
    	String buff;
        while((buff=in.readLine())!=null) {
	    	String fields[] = buff.split("\t");
	    	try {
	      double rweight =Double.parseDouble(fields[2]);
	      if(rweight>maxweight) maxweight=rweight;
	      
    	confusionMatrix.put(fields[0]+JOOIN_STRING+fields[1],rweight);
	      }catch(Exception e) {
	    	  System.err.println("Error reading weight on confusionMatrix file:"+filename);
	    	  System.err.println("AT LINE:"+buff);
	    	  e.printStackTrace();
	    	  System.exit(-1);
	      }
    	
        }
    }
    catch(Exception e){
     System.err.println("Error opening file:"+filename);

   	  e.printStackTrace();
   	  System.exit(-1);
    }
    }
    
	//@Override
	public double similarity(String[] featureNames, Word proposedWord,
			Word referenceWord) {
		boolean hold=true;
		int i=0;
		double w=0;
		while(i<featureNames.length && hold) {
			
		String featureName = featureNames[i];
		if(proposedWord.getFeature(featureName).compareTo(referenceWord.getFeature(featureName))==0) return 1;
		Double res = confusionMatrix.get(proposedWord.getFeature(featureName)+JOOIN_STRING+ referenceWord.getFeature(featureName));
		if(res==null)  {
			System.err.println("NOT found:"+proposedWord.getFeature(featureName)+JOOIN_STRING+ referenceWord.getFeature(featureName));
			w += defaultValue;
			}
		else {
			System.err.println("Found:"+proposedWord.getFeature(featureName)+JOOIN_STRING+ referenceWord.getFeature(featureName)+" -> "+res);
			w += res/maxweight;
		}
		++i;
		}
		//Normalize
      return w / featureNames.length;
	}
	
	public String getClassName() {
		return SimilarityConfusionMatrix.class.getName();
	}

	public void setReversed(boolean reversed) {}
	}

