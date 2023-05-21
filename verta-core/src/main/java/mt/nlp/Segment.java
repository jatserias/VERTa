package mt.nlp;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import lombok.Getter;
import lombok.Setter;
import mt.NgramMatch;

@Setter
@Getter
public class Segment {

	private List<Sentence> sentences;
	
	private List<TimeExpressions> timex;

	private double sentiment;

	private double depScore;

	private Set<String> nel;

	private double lmnorm;

	private float lm;


	public Segment() {
		timex = new ArrayList<TimeExpressions>();
		setSentences(new Vector<Sentence>());
		setNel(new HashSet<String>());
	}

	public void addDepScore(double depScore) {
		this.depScore = this.depScore == 0 ? depScore : ((this.depScore + depScore) / 2);
	}

	public void addNEL(String enel) {
		this.getNel().add(enel);
	}

	public void addSen(Sentence s) {

		getSentences().add(s);

	}

	public static Ngram[] ngram(Segment seg, int ngramSize) {
		Vector<Ngram> res = new Vector<Ngram>();
		for (Sentence s : seg.getSentences())
			for (Ngram ng : NgramMatch.ngram(s, ngramSize))
				res.add(ng);
		return res.toArray(Ngram.EMPTY_NGRAM);
	}

	/// return max size of the segment sentences
	public int segSize() {
		int max = 0;
		for (Sentence s : getSentences()) {
			if (s.size() > max)
				max = s.size();
		}
		return max;
	}

	public Sentence toSentence() {
		int woffset = 0;
		Sentence res = new Sentence();

		/// propagate
		res.setTimex(timex);
		res.setNel(getNel());
		res.setSentimentScore(sentiment);
		res.setDepscore(depScore);
		res.setLm(getLm());
		res.setLmn(getLmnorm());

		int nsen = 1;
		for (Sentence s : getSentences()) {
			for (Word w : s) {
				Word w1 = new Word(w);
				w1.id = "x" + nsen + "=" + (Integer.parseInt(w1.getFeature("ID")) + woffset);
				w1.setFeature("ID", "" + (Integer.parseInt(w1.getFeature("ID")) + woffset));
				String head = w1.getFeature("DEPHEAD");
				w1.setFeature("DEPHEAD",
						"" + (head.startsWith("_") ? head : (Integer.parseInt(w.getFeature("DEPHEAD"))) + woffset));

				res.add(w1);
			}
			nsen++;
			woffset += s.size();
		}

		return res;
	}

	public void dump(PrintStream out) {
		out.println("<SEGMENT>");
		for (Sentence s : getSentences())
			Sentence.dump(s, out);
		out.println("</SEGMENT>");
	}

	public void timexAdd(TimeExpressions timeExpressions) {
		timex.add(timeExpressions);
	}


}
