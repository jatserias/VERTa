package mt;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import edu.smu.tspell.wordnet.SynsetType;
import mt.core.Similarity;
import mt.core.WnBaseSimilarity;
import mt.nlp.Word;
import verta.wn.JABSynset;
import verta.wn.WordNetAPI;

class SimilarityHypernymWnMFSTest {

	@Test
	void test_mfs() {
		String word_form = "a";
		SynsetType pos = SynsetType.NOUN;
		WordNetAPI wn = Mockito.mock(WordNetAPI.class);
		JABSynset s1 = Mockito.mock(JABSynset.class);
		String [] wf1 = {"a"};
		Mockito.when(s1.getWordForms()).thenReturn(wf1);
		Mockito.when(s1.getTagCount("a")).thenReturn(7);
		
		JABSynset s2 = Mockito.mock(JABSynset.class);
		String [] wf2 = {"a", "b"};
		Mockito.when(s2.getWordForms()).thenReturn(wf2);
		Mockito.when(s2.getTagCount("a")).thenReturn(5);
		Mockito.when(s2.getTagCount("b")).thenReturn(3);
		
		JABSynset s3 = Mockito.mock(JABSynset.class);
		String [] wf3 = {"a"};
		Mockito.when(s3.getWordForms()).thenReturn(wf3);
		Mockito.when(s3.getTagCount("a")).thenReturn(5);	
		
		JABSynset[] res_wn = {
				s1, s2, s3
		};
		Mockito.when(wn.getSynsets(word_form, pos)).thenReturn(res_wn);
		SimilarityHypernymWnMFS sim = new SimilarityHypernymWnMFS(word_form);
		WnBaseSimilarity.wn = wn;
		
		JABSynset[] res = sim.getMFS(word_form, pos);
		JABSynset[] expected_res = {s2};
		
		assertArrayEquals(expected_res, res);
	}

	@Disabled("untill we find why we avoid same synset")
	@Test
	public void test_INNERsimilarity() {
		
		Word proposedWord = new Word("1", "perro").setFeature("LEMMA", "perro");
		Word referenceWord = new Word("2", "can").setFeature("LEMMA", "can");
	
		
		String featureReference = "LEMMA";
		SynsetType pos = SynsetType.NOUN;
		
		WordNetAPI wn = Mockito.mock(WordNetAPI.class);
		
		JABSynset s1 = Mockito.mock(JABSynset.class);
		String [] wf1 = {"perro"};
		Mockito.when(s1.getWordForms()).thenReturn(wf1);
		Mockito.when(s1.getTagCount("perro")).thenReturn(7);
		
		JABSynset s2 = Mockito.mock(JABSynset.class);
		String [] wf2 = {"can", "perro"};
		Mockito.when(s2.getWordForms()).thenReturn(wf2);
		Mockito.when(s2.getTagCount("perro")).thenReturn(8);
		Mockito.when(s2.getTagCount("can")).thenReturn(3);
		
		JABSynset s3 = Mockito.mock(JABSynset.class);
		String [] wf3 = {"animal"};
		Mockito.when(s3.getWordForms()).thenReturn(wf3);
		Mockito.when(s3.getTagCount("animal")).thenReturn(8);
			
		JABSynset[] res_wn_perro = {
				s1, s2
		};
		JABSynset[] res_wn_can = {
				s2
		};
		JABSynset[] ancestors = {
				s3
		};
		Mockito.when(wn.getSynsets("perro", pos)).thenReturn(res_wn_perro);
		Mockito.when(wn.getSynsets("can", pos)).thenReturn(res_wn_can);
		// @TODO same wordnet synset is not recognized
		Mockito.when(s2.getHypernyms()).thenReturn(ancestors);
		Mockito.when(s1.getHypernyms()).thenReturn(ancestors);
		
		SimilarityHypernymWnMFS sim = new SimilarityHypernymWnMFS(featureReference);
		WnBaseSimilarity.wn = wn;
		String[] featureNames = {featureReference};
		SynsetType[] lpos = {pos};
		double res = sim.INNERsimilarity(featureNames, proposedWord, referenceWord,  lpos);
		// TODO sim.MULTILEVEL = true;
		assertEquals("Innersimilarity", Similarity.MAXVAL, res, 0.0001);
	}
	
	@Test
	public void test_nsearchLists() {
		JABSynset s1 = Mockito.mock(JABSynset.class);
		JABSynset s2 = Mockito.mock(JABSynset.class);
		JABSynset s3 = Mockito.mock(JABSynset.class);
		JABSynset[] referenceSynsets =  { s1 };
		JABSynset[] hypos = {s2, s1, s3};
		assertTrue(SimilarityHypernymWnMFS.nsearchLists(referenceSynsets, hypos)); 
	}
	
}
