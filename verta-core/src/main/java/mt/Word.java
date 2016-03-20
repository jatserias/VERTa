package mt;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 
 * what ever a word is
 * @author jordi
 *
 */
public class Word {

   HashMap<String,String> features;
	String id;
	
	public Word(String id) { this.id=id;features= new HashMap<String,String>();}
	public Word(String id,String word) { this.id=id;features= new HashMap<String,String>(); features.put("WORD",word);}
	
	public Word(Word w) {
		this.id=w.id;
		this.features= (HashMap<String, String>) w.features.clone();
	}
	public void setFeature(String featureName, String featureValue) {features.put(featureName,featureValue);}
    public String getFeature(String featureName) {
	 return features.get(featureName); 
   }
    
    public String toString() {
    	StringBuffer res = new StringBuffer();
    	res.append("id:"+id+'\n');
    	for(Entry<String, String> feat:features.entrySet()) {
    		res.append(feat.getKey()+":"+feat.getValue()+"\n");
    	}
    	res.append("\n");
    	return res.toString();
    }
    
    public void dump(PrintStream out){
    	dump(out,features.keySet());
    }
    public void dump(PrintStream out,Set<String> feautureList){
    	 out.println("<word id=\""+XMLFormater.encodeXMLString(id)+"\">");
    		for(String feat:feautureList) {
    			out.print("<feat name=\""+feat+"\">"+XMLFormater.encodeXMLString(features.get(feat))+"</feat>");

    		}
    	out.println("</word>");
    }

	public String[] getFeatures(String[] featureNames) {
		String[] features = new String[featureNames.length];
		for(int i=0;i<featureNames.length;++i) features[i]=getFeature(featureNames[i]);
		return features;
	}
	public String getText() {	
		return features.get("WORD");
	}
 }
