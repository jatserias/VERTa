package junit;

import mt.SimilarityHypernymWn;
import mt.SimilaritySubstring;
import mt.SimilaritySynonymWnPos;
import mt.Word;

import org.junit.Test;

import junit.framework.TestCase;


public class MetricTest extends TestCase{
	@Test
	public void testVaris() {
		SimilaritySubstring sm = new SimilaritySubstring("1");
		Word referenceWord = new Word("ref");
		String featlemma="lemma";
		String featpos="pos";
		String[] featlemmaArray = {"lemma"};
		String[] featlemmaposArray = {"lemma","pos"};
		
		referenceWord.setFeature(featlemma,"a");
		Word proposedWord = new Word("tar");
		proposedWord.setFeature(featlemma,"to");
		Word proposedWord2 = new Word("tar2");
		proposedWord2.setFeature(featlemma,".");
		Word referenceWord2 = new Word("ref");
		referenceWord2.setFeature(featlemma,"t");
		
		
		assertEquals("a vs to",0.0,sm.similarity(featlemmaArray, proposedWord, referenceWord));
		assertEquals("a vs .",0.0,sm.similarity(featlemmaArray, proposedWord2, referenceWord));
		assertEquals("to vs a",0.0,sm.similarity(featlemmaArray, referenceWord, proposedWord));
		assertEquals(". vs a",0.0,sm.similarity(featlemmaArray, referenceWord, proposedWord2));
		
		assertEquals("to vs t",0.0,sm.similarity(featlemmaArray, proposedWord2, referenceWord2));
		
		SimilarityHypernymWn wnsm = new SimilarityHypernymWn("MULTILEVEL");
		Word cat= new Word("cat");
		cat.setFeature(featlemma,"cat");
		Word felines = new Word("felines");
		felines.setFeature(featlemma,"felines");
		Word carnivore = new Word("carnivore");
		carnivore.setFeature(featlemma, "carnivore");
		
		assertEquals("wn hyper cat felines",1.0,wnsm.similarity(featlemmaArray, cat, felines));
		assertEquals("wn hyper felines cat",0.0,wnsm.similarity(featlemmaArray, felines,cat));
		
		assertEquals("wn hyper cat carnivore",1.0,wnsm.similarity(featlemmaArray, cat, carnivore));
		wnsm.MULTILEVEL=false;
		assertEquals("wn hyper cat carnivore DIRECT",0.0,wnsm.similarity(featlemmaArray, cat, carnivore));
		
		// Reverse test
		wnsm.setReversed(true);
		assertEquals("wn hyper felines cat reversed",1.0,wnsm.similarity(featlemmaArray, felines,cat));
		wnsm.MULTILEVEL=true;
		assertEquals("wn hyper cat carnivore",1.0,wnsm.similarity(featlemmaArray, carnivore, cat));
		
		//Checking sinonims
		SimilaritySynonymWnPos wnsinp = new SimilaritySynonymWnPos();
		Word serious= new Word("serious");
		serious.setFeature(featlemma,"serious");
		serious.setFeature(featpos,"JJ");
		Word dangerous= new Word("dangerous");
		dangerous.setFeature(featlemma,"dangerous");
		dangerous.setFeature(featpos,"JJ");
		assertEquals("wn sinownpos serious dangerous",1.0,wnsinp.similarity(featlemmaposArray, serious, dangerous));
		
		
	}
}
