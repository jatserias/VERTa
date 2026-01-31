package mt.nlp;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import mt.nlp.io.CONLLformat;
import mt.nlp.io.ReaderCONLL;

class SentenceTest {


	static final String sentence_proposed_ana = "1 El       el       DA0MS0  DA  pos=determiner|type=article|gen=masculine|num=singular                 -     - - 2 spec     - -\n"
			+ "2 perro    perro    NCMS000 NC  pos=noun|type=common|gen=masculine|num=singular                        -     - - 6 suj      - -\n"
			+ "3 de       de       SP      SP  pos=adposition|type=preposition                                        -     - - 2 sp       - -\n"
			+ "4 el       el       DA0MS0  DA  pos=determiner|type=article|gen=masculine|num=singular                 -     - - 5 spec     - -\n"
			+ "5 Sr._Jose sr._jose NP00G00 NP  pos=noun|type=proper|neclass=location                                  B-LOC - - 3 sn       - -\n"
			+ "6 come     comer    VMIP3S0 VMI pos=verb|type=main|mood=indicative|tense=present|person=3|num=singular -     - - 0 sentence - -\n"
			+ "7 una      uno      DI0FS0  DI  pos=determiner|type=indefinite|gen=feminine|num=singular               -     - - 8 spec     - -\n"
			+ "8 manzana  manzana  NCFS000 NC  pos=noun|type=common|gen=feminine|num=singular                         -     - - 6 cd       - -\n"
			+ "";

	static final String sentence_reference_ana = "1 Los                     el                      DA0MP0  DA pos=determiner|type=article|gen=masculine|num=plural   -      - - 2 spec     - -\n"
			+ "2 perros                  perro                   NCMP000 NC pos=noun|type=common|gen=masculine|num=plural          -      - - 0 sentence - -\n"
			+ "3 de                      de                      SP      SP pos=adposition|type=preposition                        -      - - 2 sp       - -\n"
			+ "4 el                      el                      DA0MS0  DA pos=determiner|type=article|gen=masculine|num=singular -      - - 5 spec     - -\n"
			+ "5 Sr._Pepe_comen_manzanas sr._pepe_comen_manzanas NP00V00 NP pos=noun|type=proper|neclass=other                     B-MISC - - 3 sn       - -\n"
			+ "";


	@Test
	void test_constructor() {
		Sentence sentence = getSentenceEx1();
		assertEquals("the cat eats fish .", sentence.getText());
	}

	public static Sentence getSentenceEx1() {
		Sentence sentence = new Sentence();
		String words[] = {"the", "cat", "eats", "fish", "."};
		for(String word : words)
			sentence.add(new Word(word, word));
		return sentence;
	}

	public static Sentence getSentenceExConll(String conllText) throws Exception {
		CONLLformat fmt = new CONLLformat("jar:/conf/conllFreeling.fmt");

		BufferedReader hyp = new BufferedReader(new StringReader(conllText));
		return  ReaderCONLL.read(hyp, fmt);
	}

	public static Sentence getSentenceEx2() throws Exception {
		return getSentenceExConll(sentence_proposed_ana);
	}
	public static Sentence getSentenceEx3() throws Exception {
		return getSentenceExConll(sentence_reference_ana);
	}
}
