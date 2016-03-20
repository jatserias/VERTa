package mt;

public class Wordnet {
	public static WordNetAPI wn = (System.getProperty("WNLANG")==null ||  System.getProperty("WNLANG").compareToIgnoreCase("EN")==0) ? new WordNetApiEnImpl() : new WordNetApiSpImpl(System.getProperty("WNLANG"));
		
	static WordNetAPI getWordNet() {
		return wn;
	}
 
}
