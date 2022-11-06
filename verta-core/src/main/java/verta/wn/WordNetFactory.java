package verta.wn;

public class WordNetFactory {

    public static IWordNet getWordNet(String lang, String path) {
        return (lang.compareToIgnoreCase("EN") == 0) ? new WordNetEnImpl() : new WordNetSpImpl(lang, path);
    }

}
