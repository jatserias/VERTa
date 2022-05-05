package mt;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import edu.smu.tspell.wordnet.SynsetType;
import mt.nlp.Word;
import verta.wn.JABSynset;
import verta.wn.WordNetAPI;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MetricTest {
	
	@Test
	public void old_Varis() {
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
		
		
		assertEquals(0.0,sm.similarity(featlemmaArray, proposedWord, referenceWord), "a vs to");
		assertEquals(0.0,sm.similarity(featlemmaArray, proposedWord2, referenceWord), "a vs .");
		assertEquals(0.0,sm.similarity(featlemmaArray, referenceWord, proposedWord), "to vs a");
		assertEquals(0.0,sm.similarity(featlemmaArray, referenceWord, proposedWord2), ". vs a");
		
		assertEquals(0.0,sm.similarity(featlemmaArray, proposedWord2, referenceWord2),"to vs t");
		
		WordNetAPI wn = Mockito.mock(WordNetAPI.class);
		JABSynset s1_cat = Mockito.mock(JABSynset.class);
		JABSynset s1_felines = Mockito.mock(JABSynset.class);
		JABSynset s1_carnivore = Mockito.mock(JABSynset.class);
		

		JABSynset senses_cat[]  = {s1_cat};
		JABSynset senses_felines[]  = {s1_felines};
		JABSynset senses_carnivore[]  = {s1_carnivore};
		
		JABSynset s1_cat_hypernyms[]  = {s1_felines};
		JABSynset s1_felines_hypernyms[]  = {s1_carnivore};
		JABSynset s1_carnivore_hypernyms[]  = {};
			
		JABSynset no_synset[]  = {};
		
		Mockito.when(wn.getSynsets("cat", SynsetType.NOUN)).thenReturn(senses_cat);
		Mockito.when(wn.getSynsets("felines", SynsetType.NOUN)).thenReturn(senses_felines);
		Mockito.when(wn.getSynsets("carnivore", SynsetType.NOUN)).thenReturn(senses_carnivore);
		
		for(SynsetType pos:  SynsetType.ALL_TYPES) {
			if (pos != SynsetType.NOUN) {
				Mockito.when(wn.getSynsets("cat", pos)).thenReturn(no_synset);
				Mockito.when(wn.getSynsets("felines", pos)).thenReturn(no_synset);
				Mockito.when(wn.getSynsets("carnivore", pos)).thenReturn(no_synset);
			}
		}
		
		Mockito.when(s1_cat.getHypernyms()).thenReturn(s1_cat_hypernyms);
		Mockito.when(s1_felines.getHypernyms()).thenReturn(s1_felines_hypernyms);
		Mockito.when(s1_carnivore.getHypernyms()).thenReturn(s1_carnivore_hypernyms);
		
		
		SimilarityHypernymWn.wn = wn;
		
		SimilarityHypernymWn wnsm = new SimilarityHypernymWn("MULTILEVEL");
		Word cat= new Word("cat");
		cat.setFeature(featlemma,"cat");
		Word felines = new Word("felines");
		felines.setFeature(featlemma,"felines");
		Word carnivore = new Word("carnivore");
		carnivore.setFeature(featlemma, "carnivore");
		
		assertEquals(1.0, wnsm.similarity(featlemmaArray, cat, felines),"wn hyper cat felines");
		assertEquals(0.0, wnsm.similarity(featlemmaArray, felines, cat),"wn hyper felines cat");
		
		assertEquals(1.0, wnsm.similarity(featlemmaArray, cat, carnivore), "wn hyper cat carnivore");
		wnsm.MULTILEVEL=false;
		assertEquals(0.0, wnsm.similarity(featlemmaArray, cat, carnivore), "wn hyper cat carnivore DIRECT");
		
		// Reverse test
		wnsm.setReversed(true);
		assertEquals(1.0,wnsm.similarity(featlemmaArray, felines,cat), "wn hyper felines cat reversed");
		wnsm.MULTILEVEL=true;
		assertEquals(1.0,wnsm.similarity(featlemmaArray, carnivore, cat),"wn hyper cat carnivore");
		
		//Checking synonims
		
		JABSynset common_sense = Mockito.mock(JABSynset.class);
		JABSynset senses_for_serious[] = {common_sense};
		JABSynset senses_for_dangerous[] = {common_sense};
		Mockito.when(wn.getSynsets("serious", SynsetType.ADJECTIVE)).thenReturn(senses_for_serious);
		Mockito.when(wn.getSynsets("dangerous", SynsetType.ADJECTIVE)).thenReturn(senses_for_dangerous);
		SynsetType[] possible_pos = {SynsetType.ADJECTIVE};
		Mockito.when(wn.getSynsetTypeFromPos("JJ")).thenReturn(possible_pos);
		SimilaritySynonymWnPos wnsinp = new SimilaritySynonymWnPos();
		Word serious= new Word("serious");
		serious.setFeature(featlemma,"serious");
		serious.setFeature(featpos,"JJ");
		Word dangerous= new Word("dangerous");
		dangerous.setFeature(featlemma,"dangerous");
		dangerous.setFeature(featpos,"JJ");
		assertEquals(1.0,wnsinp.similarity(featlemmaposArray, serious, dangerous),"wn sinownpos serious dangerous");
		
		
	}
}
