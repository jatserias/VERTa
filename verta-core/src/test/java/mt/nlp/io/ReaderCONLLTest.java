package mt.nlp.io;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import mt.nlp.Segment;
import mt.nlp.Sentence;
import mt.nlp.TimeExpressions;
import mt.nlp.Word;

/**
 * 
%%#SEC  2
%%#TIMEX        3470    3473    DURATION        P1D
%%#TIMEX        3563    3566    DURATION        P1D
%%#TIMEX        3592    3597    DATE    2012-09-19
%%#TIMEX        3684    3689    DATE    2012-09-19
1       Oslo    NN      NNP     oslo    B-LOC   B-noun.location B-E:GPE:CITY    nn      5
2       6-2     CD      NNP     6-2     I-LOC   I-noun.location I-E:GPE:CITY    nn      5
3       (       (       NNP     (       0       0       0       nn      5
4       AFP     NNP     NNP     afp     B-ORG   B-noun.substance        B-E:SUBSTANCE:OTHER     nn      5
5       )       )       NNP     )       0       0       0       _       0
6       -       :       :       -       0       0       0       _       0
7       Terje   NNP     NNP     terje   B-PER   B-noun.person   B-E:PERSON      nn      12
8       Rod     NNP     NNP     rod     I-PER   I-noun.person   I-E:PERSON      nn      12
9       Larsen  NNP     NNP     larsen  I-PER   I-noun.person   I-E:PERSON      nn      12


%%#SEG  1
%%#DEP  -74.01345825195312
%%#SENTI        0.5
%%#LM   -27.49189
%%#LMN  1.1492748316660314E-12
1       Petr    NNP     NNP     petr    0       B-noun.other    B-E:WORK_OF_ART:BOOK    nn      2
2       Čech    NNP     NNP     čech    0       I-noun.other    I-E:WORK_OF_ART:BOOK    root    0
3       :       :       :       :       0       I-noun.other    I-E:WORK_OF_ART:BOOK    _       0
4       Transfer        VB      NNP     transfer        0       I-noun.other    I-E:WORK_OF_ART:BOOK    dep     2
5       at      IN      IN      at      0       0       0       _       0
6       the     DT      DT      the     0       0       B-T:TIME        det     8
7       last    JJ      JJ      last    0       B-noun.time     I-T:TIME        amod    8
8       minute  NN      NN      minute  0       I-noun.time     I-T:TIME        prep_at 4
9       ?       .       .       ?       0       0       0       _       0

 *
 */
class ReaderCONLLTest {

	@Test
	void test_header_NEL() {
		Segment seg = new Segment();
		String buff = "%%#NEL\tjohn_f_bush madona bruce";
		ReaderCONLL.processHeader(seg, buff);
		//@TODO comparison take order into account
		String expected_result[] = {"madona", "john_f_bush", "bruce"};
		assertArrayEquals(expected_result, seg.getNel().toArray(),  String.format("simple NEL"));
	}
	
	@Test
	void test_header_TIMEX() {
		Segment seg = new Segment();
		String buff = "%%#TIMEX\t3470\t3473\tDURATION\tP1D";
		ReaderCONLL.processHeader(seg, buff);
		TimeExpressions expected_result[] = { new TimeExpressions(3470, 3473,  "P1D", "DURATION")};
		assertArrayEquals(expected_result, seg.getTimex().toArray(),  String.format("simple TIMEX"));
	}

	@Test
	void test_header_LM() {
		Segment seg = new Segment();
		String buff = "%%#LM\t-27.49189";
		ReaderCONLL.processHeader(seg, buff);
		assertEquals(-27.49189, seg.getLm(),  0.0000001, String.format("LM"));
	}
	
	@Test
	void test_header_LMN() {
		Segment seg = new Segment();
		String buff = "%%#LMN\t1.1492748316660314E-12";
		ReaderCONLL.processHeader(seg, buff);
		assertEquals(1.1492748316660314E-12, seg.getLmnorm(),  0.0000001, String.format("LMN"));
	}
	
	@Test
	void test_header_SENTI() {
		Segment seg = new Segment();
		String buff = "%%#SENTI\t0.5";
		ReaderCONLL.processHeader(seg, buff);
		assertEquals(0.5, seg.getSentiment(),  0.0000001, String.format("SENTI"));
	}
	
	@Test
	void test_header_DEP() {
		Segment seg = new Segment();
		String buff = "%%#DEP\t-74.01345825195312";
		ReaderCONLL.processHeader(seg, buff);
		assertEquals(-74.01345825195312, seg.getDepScore(),  0.000000000000001, String.format("DEP"));
	}
	
	
	@Test
	void test_sentence_reader() throws Exception {
		CONLLformat pfmt = new CONLLformat();
		
		pfmt.setFormatFilename("test");
		String [] features = {"ID", "WORD", "POS"};
		pfmt.featureNames = features;
		
		BufferedReader sin = new BufferedReader(new StringReader("1\tOslo\tNN\n"
				+ "2\t6-2\tCD\n"
				+ "3\t(\tNNP"));
		Sentence sentence = ReaderCONLL.read(sin,  pfmt);
		Sentence expected_sentence = new Sentence();
		
		expected_sentence.add(new Word("1", "Oslo").setFeature("ID", "1").setFeature("POS", "NN"));
		expected_sentence.add(new Word("2", "6-2").setFeature("ID", "2").setFeature("POS", "CD"));
		expected_sentence.add(new Word("3", "(").setFeature("ID", "3").setFeature("POS", "NNP"));
		
		// compare words no annotation
		assertArrayEquals(expected_sentence.toArray(), sentence.toArray());
		
	}
}
