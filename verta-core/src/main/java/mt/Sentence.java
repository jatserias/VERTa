package mt;

import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class Sentence extends Vector<Word>{

	public double sentimentScore;
	public List<TimeExpressions> timex;
	public Set<String> nel;
	public double depscore;
	public float lm;
	public double lmn;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void dump(PrintStream out) {
		out.println("<sen>");
		for(Word w: this) {
			w.dump(out);
		}
		out.println("</sen>");
	}

	public String getText() {
		StringBuffer res = new StringBuffer();
		int i=0;
		for(Word w :this) {
			if(i>0) res.append(' ');
		    res.append(w.getText());
			++i;
		}
		return res.toString();
	}
	
	public String toString() {
		return getText();
	}
	
	public static void main(String args[]) {
		Sentence s = new Sentence();
		s.add(new Word("1","hola"));
		s.add(new Word("2","adeu"));
		System.err.println(s.toString());
		System.err.println(s.getText());
	}
}
