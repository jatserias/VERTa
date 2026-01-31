package verta.wn;


public class WordNetFactory {

    public static IWordNet getWordNet(String language, String path) {
        return (language.compareToIgnoreCase("EN") == 0) ? new WordNetEnImpl() : new WordNetSpImpl(language, path);
    }

}
