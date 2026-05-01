package mt.core;

import java.io.PrintStream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mt.core.TriplePatternMatcher.TripleMatchOperator;
import mt.nlp.Triples;

/// pattern to match triples
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripleMatchPattern {

	/// match operator for head
	private TripleMatchOperator head;
	/// mod match operator
	private TripleMatchOperator mod;
	/// Label operator if null LabelSet apply
	private TripleMatchOperator label;
	/// Labelset for source triple
	private LabelSet labelSet;
	/// LabelSet for target triple
	private LabelSet targetLabelSet;
	/// weight of the pattern
	private double weight;

	
	/**
	 * TripleMatchPattern(TripleMatchOperator head, TripleMatchOperator label,
	 * TripleMatchOperator mod, double weight) {
	 * this.head = head;
	 * this.mod = mod;
	 * this.weight = weight;
	 * this.label = label;
	 * this.labelSet = null;
	 * this.targetLabelSet = null;
	 * }
	 **/
	/**
	 * using sets the _% will not work _X -> _% (possible)
	 * 
	 * @param x
	 * @param y
	 * @param label_match
	 * @return
	 */
	private boolean matchLabel(final Triples x, final Triples y, boolean label_match) {
		return (

		// check group only for x
		(label == null && ((getLabelSet() == null || getLabelSet().contains(x.getLabel())
				|| checkLabel(getLabelSet(), x.getLabel()))
				&& (targetLabelSet == null || targetLabelSet.contains(y.getLabel())
						|| checkLabel(targetLabelSet, y.getLabel()))

		)) || ((label != null) && (label_match || label == TripleMatchOperator.O)));
	}

	static public boolean checkLabel(LabelSet labelSet, String label) {
		return labelSet.contains(label) || labelSet.contains(TriplesMatcher.getSubLabel(label) + "_%");
	}

	/**
	 * label match => label are equal or match rules
	 */
	public boolean match(final Triples x, final Triples y, boolean label_match, boolean source_match,
			boolean target_match) {
		return ((matchLabel(x, y, label_match)) && (head == TripleMatchOperator.O || source_match) &&
				(mod == TripleMatchOperator.O || target_match));
	}

	public void dump(PrintStream err) {
		err.println(this.toString());
	}

	public String toString() {
		StringBuffer err = new StringBuffer();
		err.append("(");
		if (label != null)
			err.append(label.name());
		if (getLabelSet() != null)
			err.append(" " + getLabelSet().getLabels() + " " + getLabelSet().getId() + " setw:" + getLabelSet().getWeight());
		if (targetLabelSet != null)
			err.append(" " + targetLabelSet.getLabels() + " labelw:" + targetLabelSet.getWeight());
		err.append(",");
		err.append(head == null ? "NULL" : head.name());
		err.append(",");
		err.append(mod == null ? "NULL" : mod.name());
		err.append(") : " + getWeight());
		return err.toString();
	}
}