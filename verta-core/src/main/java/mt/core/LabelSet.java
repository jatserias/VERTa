package mt.core;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

public class LabelSet {
	public Set<String> labels;
	public Double w;
	public String id;

	public LabelSet(String buff) {
		String[] wv = buff.split("[\t ]+");
		w = Double.parseDouble(wv[0]);
		id = wv[1];
		labels = new HashSet<String>();
		for (int i = 2; i < wv.length; ++i) {
			labels.add(wv[i].toLowerCase());
		}
	}

	public boolean contains(String label) {
		return labels.contains(label);
	}

	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("W:" + w + "\n");
		s.append("ID:" + id + "\n");
		for (String label : labels) {
			s.append("L:" + label + "\n");
		}
		return s.toString();
	}

	public void dump(PrintStream s) {
		s.println("W:" + w);
		s.println("ID:" + id);
		for (String label : labels) {
			s.println("L:" + label);
		}
	}
}