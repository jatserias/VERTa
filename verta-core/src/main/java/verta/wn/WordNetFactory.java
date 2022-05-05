package verta.wn;

public class WordNetFactory {

	
	public static WordNetAPI getWordNet(String lang, String path) {
		return (lang.compareToIgnoreCase("EN")==0) ? new WordNetApiEnImpl() : new WordNetApiSpImpl(lang, path);
	}
 
}
