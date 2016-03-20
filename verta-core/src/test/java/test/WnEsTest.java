package test;

import java.io.IOException;

import junit.framework.Assert;
import mt.JABSynset;
import mt.SimilarityHypernymWnPos;
import mt.WnBaseSimilarity;
import mt.Word;
import mt.WordNetApiEnImpl;
import mt.WordNetApiSpImpl;

import org.junit.Test;

import edu.smu.tspell.wordnet.SynsetType;
import test.WnTest;

public class WnEsTest {
 
	
		@Test
		public void test() throws IOException {
			
			String[] featureNamesLP= {"LEMMA","POS"};
			
			System.getProperties().setProperty("WNLANG","es");
			System.getProperties().setProperty("wordnet.database.dir","/usr/local/WordNet-3.0/dict/");
			WordNetApiSpImpl wn = new WordNetApiSpImpl("es");
			
			SimilarityHypernymWnPos sx = new mt.SimilarityHypernymWnPos("MULTILEVEL");
			SimilarityHypernymWnPos.wn=wn;
			
			
			String wordForm = "forma";
			JABSynset[] ls ;		
				SynsetType pos = SynsetType.NOUN;
				ls = SimilarityHypernymWnPos.wn.getSynsets(wordForm, pos);
				for(JABSynset s:ls) {
					System.err.println(s);
				}
				
				
				
			Word formasNWord = WnTest.createWord("1a","formas","forma", "N");
			Word execucionNWord = WnTest.createWord("2a","ejecución","ejecución","N");
//			System.err.println(" formas hyp execucion "+sx.similarity(featureNamesLP, formasNWord, execucionNWord));
//			System.err.println(" execucion hyp formas "+sx.similarity(featureNamesLP,  execucionNWord,formasNWord));
			Assert.assertEquals(" formas hyp execucion ", 1.0,sx.similarity(featureNamesLP, formasNWord, execucionNWord));
			Assert.assertEquals(" execucion hyp formas", 0.0,sx.similarity(featureNamesLP,  execucionNWord,formasNWord));
			
			Word formasVWord = WnTest.createWord("1a","formas","formar", "V");
			Word prepararVWord = WnTest.createWord("2a","preparar","preparara","V");
			Assert.assertEquals("formar hyp preparar", 0.0,sx.similarity(featureNamesLP, formasVWord, prepararVWord));
			Assert.assertEquals("peparar hyp formar", 1.0,sx.similarity(featureNamesLP,  prepararVWord,prepararVWord));
//System.err.println("formar hyp preparar"+sx.similarity(featureNamesLP, formasVWord, prepararVWord));
//System.err.println("peparar hyp formar"+sx.similarity(featureNamesLP,  prepararVWord,prepararVWord));	
 }
}
