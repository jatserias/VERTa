package mt.nlp;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import verta.xml.XMLFormater;

/**
 * 
 * what ever a word is
 * 
 */
public class Word {

	HashMap<String, String> features;
	String id;

	public Word(String id) {
		this.id = id;
		features = new HashMap<String, String>();
	}

	public Word(String id, String word) {
		this.id = id;
		features = new HashMap<String, String>();
		features.put("WORD", word);
	}

	public Word(Word w) {
		this.id = w.id;
		this.features = new HashMap<String, String>(w.features);
	}

	public Word setFeature(String featureName, String featureValue) {
		features.put(featureName, featureValue);
		return this;
	}

	public String getFeature(String featureName) {
		String res = features.get(featureName);
		if (res == null) {
			return "NOPE";
		}
		return res;
	}

	public String toString() {
		StringBuffer res = new StringBuffer();
		res.append("id:" + id + '\n');
		for (Entry<String, String> feat : features.entrySet()) {
			res.append(feat.getKey() + ":" + feat.getValue() + "\n");
		}
		res.append("\n");
		return res.toString();
	}

	public static void dump(Word w, PrintStream out) {
		dump(w, out, w.features.keySet());
	}

	public static void dump(Word w, PrintStream out, Set<String> feautureList) {
		out.println("<word id=\"" + XMLFormater.encodeXMLString(w.id) + "\">");
		for (String feat : feautureList) {
			out.print("<feat name=\"" + feat + "\">" + XMLFormater.encodeXMLString(w.features.get(feat)) + "</feat>");

		}
		out.println("</word>");
	}

	public String[] getFeatures(String[] featureNames) {
		String[] features = new String[featureNames.length];
		for (int i = 0; i < featureNames.length; ++i)
			features[i] = getFeature(featureNames[i]);
		return features;
	}

	public String getText() {
		return features.get("WORD");
	}
	
	@Override
    public boolean equals(Object obj)
    {
          
		if(this == obj) return true;
          
        if(obj == null || obj.getClass()!= this.getClass())
            return false;
          
        Word word = (Word) obj;
          
        return word.toString().equals(this.toString());
    }
	
	public int hashCode() {
		return this.toString().hashCode();
	}
}
