package mt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashSet;

import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import mt.core.DistanceMatrix;
import mt.core.MetricActivationCounter;
import mt.core.MetricResult;
import mt.core.SentenceAlignment;
import mt.core.SimilarityResult;
import mt.core.Verta;
import mt.nlp.Ngram;
import mt.nlp.Sentence;
import mt.nlp.Word;
import verta.wn.WordNetAPI;

public class NgramTest {

	public void old_main_testVaris() {

		String[] words = { "the", "cat", "eats", "fish", "." };
		String[] words2 = { "the", "dog", "eats", "fish", "." };

		int[][] ngram1 = { { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 4 }, { 0, 1, 2 }, { 1, 2, 3 }, { 2, 3, 4 },
				{ 0, 1, 2, 3 }, { 1, 2, 3, 4 }, { 0, 1, 2, 3, 4 } };

		HashSet<Ngram> ngramset1 = new HashSet<Ngram>();
		WordNetAPI wn = Mockito.mock(WordNetAPI.class);

		// crear ngrams
		Verta mt = new Verta("test", new BufferedReader(new StringReader(
				"GROUP\tTEST\t1\t1\tmt.WordMetric\n" + "1\tWORD\t100\tmt.SimilarityEqual\n" + "FGROUP\n")), wn);
		Sentence s1 = new Sentence();
		int n = 0;
		for (String s : words)
			s1.add(new Word("" + (n++), s));
		Sentence s2 = s1;
		Sentence s3 = new Sentence();
		n = 0;
		for (String s : words2)
			s3.add(new Word("" + (n++), s));

		Sentence.dump(s1, System.err);
		Sentence.dump(s2, System.err);

		// MetricResult r = mt.similarity(s1,s2, System.err);

		// double[][] dist= new double[words.length][words2.length];
		DistanceMatrix dist = new DistanceMatrix(s1, s1);
		for (int i = 0; i < words.length; ++i) {
			dist.setDistance(false, i, i, 1, "test");
			dist.setDistance(true, i, i, 1, "test");
		}

		MetricResult r = mt.similarity(s1, s2);

		double ngramprec;
		MetricActivationCounter counter = new MetricActivationCounter();
		NgramMatch ngram = new NgramMatch(counter, "2");

		for (int i = 2; i < s1.size(); ++i) {
			ngramprec = NgramMatch.compareNgrams(i, true, s1, s1, dist, System.err);
			System.err.println("i:" + i + ":" + ngramprec + " #ngrams:" + NgramMatch.sumatori(i, s1.size()));
			Assertions.assertEquals(NgramMatch.sumatori(i, s1.size()), ngramprec, 0.01,
					String.format("Ngram prec %d", i));
			SimilarityResult x = ngram.similarity(s1, s1, dist, System.err);
			System.err.println("TRACE" + x);
			ngramprec = NgramMatch.compareNgrams(i, false, s1, s1, dist, System.err);
			Assertions.assertEquals(ngramprec, NgramMatch.sumatori(i, s1.size()), 0.01);
		}

		ngramprec = NgramMatch.compareNgrams(100, false, s1, s2, dist, System.err);
		System.err.println("PREC:" + ngramprec + ":" + NgramMatch.sumatori(100, s1.size()));
		ngramprec = ngramprec / NgramMatch.sumatori(100, s1.size());

		// we will need to reverse dist
		double ngramrec = NgramMatch.compareNgrams(100, true, s2, s1, dist, System.err);
		ngramrec = ngramrec / NgramMatch.sumatori(1, 100, s2.size());
		System.err.println("PREC:" + ngramrec + ":" + NgramMatch.sumatori(100, s1.size()));

		assertEquals(ngramprec, 1.0, 0.000001);
		assertEquals(ngramrec, 1.0, 0.000001);

		/**
		 * the dog eats fish vs the cat eats fish
		 * 
		 * common ngrams
		 * 
		 * s=2 : 1 / 3 s=3 : 0 / 2 s=4 : 0 / 1
		 * 
		 */
		r = mt.similarity(s1, s3);

		SentenceAlignment align = dist; // new AlignmentBuilderFirstLeft2Rigth().build(dist);
		// OLD
		// Integer align[] = new Integer[Math.max(s1.size(), s2.size())];
		// TODO FIX JUNIT
		// for(int i=0;i<align.length;++i) align[i]=-1;
		// double prec=mt.wms.get(0).sentenceSimilarity(align,dist,false,
		// s1,s2,mt.wms.get(0));
		// for(int i=0;i<align.length;++i) align[i]=-1;
		// double rec=sentenceSimilarity(align, dist,true,s1,s2,mt.wms.get(0));

		ngramprec = NgramMatch.compareNgrams(100, false, s1, s2, align, System.err);
		System.err.println("PREC:" + ngramprec + ":" + NgramMatch.sumatori(100, s1.size()));
		ngramprec = ngramprec / NgramMatch.sumatori(100, s1.size());
		// we will need to reverese dist??
		ngramrec = NgramMatch.compareNgrams(100, true, s2, s1, align, System.err);
		ngramrec = ngramrec / NgramMatch.sumatori(100, s2.size());
		System.err.println("PREC:" + ngramrec + ":" + NgramMatch.sumatori(100, s1.size()));

		assertEquals(ngramprec, 1.0, 0.000001);
		assertEquals(ngramrec, 1.0, 0.000001);
	}
}
