package mt.nlp;

import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class Sentence extends Vector<Word> {

	private double sentimentScore;
	private List<TimeExpressions> timex;
	private Set<String> nel;
	private double depscore;
	private float lm;
	private double lmn;

	private static final long serialVersionUID = 1L;

	public static void dump(Sentence s, PrintStream out) {
		out.println("<sen>");
		for (Word w : s) {
			Word.dump(w, out);
		}
		out.println("</sen>");
	}

	public String getText() {
		StringBuffer res = new StringBuffer();
		int i = 0;
		for (Word w : this) {
			if (i > 0)
				res.append(' ');
			res.append(w.getText());
			++i;
		}
		return res.toString();
	}

	public String toString() {
		// TODO check if used as return getText();
		StringBuffer res = new StringBuffer("<\n");
		for (Word word : this) {
			res.append(word + "\n");
		}
		res.append(">\n");
		return res.toString();
	}

	public double getDepscore() {
		return depscore;
	}

	public void setDepscore(double depscore) {
		this.depscore = depscore;
	}

	public Set<String> getNel() {
		return nel;
	}

	public void setNel(Set<String> nel) {
		this.nel = nel;
	}

	public double getSentimentScore() {
		return sentimentScore;
	}

	public void setSentimentScore(double sentimentScore) {
		this.sentimentScore = sentimentScore;
	}

	public float getLm() {
		return lm;
	}

	public void setLm(float lm) {
		this.lm = lm;
	}

	public double getLmn() {
		return lmn;
	}

	public void setLmn(double lmn) {
		this.lmn = lmn;
	}

	public List<TimeExpressions> getTimex() {
		return timex;
	}

	public void setTimex(List<TimeExpressions> timex) {
		this.timex = timex;
	}

}
