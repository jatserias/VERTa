package test;

import java.io.IOException;

import junit.framework.Assert;

import mt.JABSynset;
import mt.Similarity;
import mt.SimilarityHypernymWnPos;
import mt.SimilaritySynonymWn;
import mt.WnBaseSimilarity;
import mt.Word;
import mt.WordNetApiEnImpl;
import mt.WordNetApiSpImpl;
import mt.Wordnet;

import org.junit.Test;

import edu.smu.tspell.wordnet.SynsetType;


public class WnTest {

	@Test
	public void test() throws IOException {
		String[] featureNames= {"WORD"};
		String[] featureNamesLP= {"LEMMA","POS"};
		System.getProperties().setProperty("WNLANG","EN");
		System.getProperties().setProperty("wordnet.database.dir","/usr/local/WordNet-3.0/dict/");
		
	String wordForm ;
	
		
		System.err.println("HERE");
		SimilaritySynonymWn x = new mt.SimilaritySynonymWn();
		
		
		Word formsNWord= createWord("1","forms","form", "N");
		Word referenceWord =  new Word("2","network");referenceWord.setFeature("LEMMA", "network");referenceWord.setFeature("POS", "N");
		Word shapeNWord =  new Word("3","shapes");shapeNWord.setFeature("LEMMA", "shape");shapeNWord.setFeature("POS", "N");
		Word shapeVWord =  new Word("3","shapes");shapeVWord.setFeature("LEMMA", "shape");shapeVWord.setFeature("POS", "V");
		
		Word nosynpod =new Word("1","make"); nosynpod.setFeature("LEMMA", "make");nosynpod.setFeature("POS", "V");
		Word vsynpod =new Word("4","form"); vsynpod.setFeature("LEMMA", "form");vsynpod.setFeature("POS", "V");
		Word asynpod =new Word("5","full"); asynpod.setFeature("LEMMA", "full");asynpod.setFeature("POS", "J");
		Word asynpoda =new Word("6","good"); asynpoda.setFeature("LEMMA", "good");asynpoda.setFeature("POS", "J");
		
		
		Assert.assertEquals("syn w1 w1=",1.0,x.similarity(featureNames, formsNWord, formsNWord));
		Assert.assertEquals("syn w1 w2=",0.0,x.similarity(featureNames, formsNWord, referenceWord));
		Assert.assertEquals("syn w2 w1=",0.0,x.similarity(featureNames,  referenceWord,formsNWord));
		Assert.assertEquals("syn w2 synw=",1.0,x.similarity(featureNames, formsNWord, shapeNWord));
		Assert.assertEquals("syn synw w2=",1.0,x.similarity(featureNames, shapeNWord, formsNWord));
		Assert.assertEquals("w synnopos w",1.0,x.similarity(featureNames, nosynpod, formsNWord));
		Assert.assertEquals("synnopos w",1.0,x.similarity(featureNames, formsNWord,nosynpod ));
		Assert.assertEquals("V synw w",1.0,x.similarity(featureNames, vsynpod,nosynpod ));
		Assert.assertEquals("w synw V",1.0,x.similarity(featureNames, nosynpod, vsynpod ));
		Assert.assertEquals("A w synw ",1.0,x.similarity(featureNames, asynpod, asynpoda));
		Assert.assertEquals("A synw w",1.0,x.similarity(featureNames, asynpoda, asynpoda));
		Assert.assertEquals("A w1-n w",0.0,x.similarity(featureNames, formsNWord, asynpoda));
		
	
		
		
		Word estructuresNWord =  new Word("4","structures");estructuresNWord.setFeature("LEMMA", "structure");estructuresNWord.setFeature("POS", "N");
		Similarity ss = new mt.SimilarityHypernymWn("MULTILEVEL");
		Assert.assertEquals("hyp w1 w1=",1.0,ss.similarity(featureNames, formsNWord, formsNWord));
		Assert.assertEquals("hyp w1 w4=",1,0,ss.similarity(featureNames, formsNWord, estructuresNWord));
		Assert.assertEquals("hyp w4 w1=",0.0,ss.similarity(featureNames, estructuresNWord, formsNWord));
		
		Similarity sh = new mt.SimilarityHyponymWn("MULTILEVEL");
		Assert.assertEquals("hypo w1 w1=",1.0,sh.similarity(featureNames, formsNWord, formsNWord));
		Assert.assertEquals("hypo w1 w4=",0.0,sh.similarity(featureNames, formsNWord, estructuresNWord));
		Assert.assertEquals("hypo w4 w1=",1.0,sh.similarity(featureNames, estructuresNWord, formsNWord));
		
		
		
		x = new mt.SimilaritySynonymWnPos();
		
		/**
		 * bug: word-form+lemma em donava la mateixa correlació que quan utilitzava word-form+lemma+synonyms 
		 */
		
		Assert.assertEquals("synpos w1 w1",1.0, x.similarity(featureNamesLP, formsNWord, formsNWord));
		Assert.assertEquals("syn w1 w2nosyn", 0.0,x.similarity(featureNamesLP, formsNWord, referenceWord));
		Assert.assertEquals("syn w synw",1.0,x.similarity(featureNamesLP, formsNWord, shapeNWord));
		Assert.assertEquals("syn synw w",1.0,x.similarity(featureNamesLP, shapeNWord, formsNWord));
		Assert.assertEquals("w synnopos w",0.0,x.similarity(featureNamesLP, nosynpod, formsNWord));
		Assert.assertEquals("synnopos w",0.0,x.similarity(featureNamesLP, formsNWord,nosynpod ));
		Assert.assertEquals("V synw w",1.0,x.similarity(featureNamesLP, vsynpod,nosynpod ));
		Assert.assertEquals("w synw V",1.0,x.similarity(featureNamesLP, nosynpod, vsynpod ));
		Assert.assertEquals("A w synw ",1.0,x.similarity(featureNamesLP, asynpod, asynpoda));
		Assert.assertEquals("A synw w",1.0,x.similarity(featureNamesLP, asynpoda, asynpoda));
		Assert.assertEquals("A w1-n w",0.0,x.similarity(featureNamesLP, formsNWord, asynpoda));
		
		ss = new SimilarityHypernymWnPos("MULTILEVEL");
		System.err.println("hyp w1 w1="+ss.similarity(featureNamesLP, formsNWord, formsNWord));
		System.err.println("hyp w1 w4="+ss.similarity(featureNamesLP, formsNWord, estructuresNWord));
		System.err.println("hyp w4 w1="+ss.similarity(featureNamesLP, estructuresNWord, formsNWord));
		System.err.println("hypV make shape ="+ss.similarity(featureNamesLP,shapeVWord, nosynpod ));
		System.err.println("hypV make shape ="+ss.similarity(featureNamesLP,nosynpod, shapeVWord ));
	}

	public static Word createWord(String id,String word, String lemma, String pos) {
		Word proposedWord = new Word(id,word); 
		proposedWord.setFeature("LEMMA",lemma);
		proposedWord.setFeature("POS", pos);
		return proposedWord;
	}
}
