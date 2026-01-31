
package mt.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mt.core.TriplePatternMatcher.TripleMatchOperator;

class TripleMatchPatternLoader {

    /// Pattern to read tpattern from text
    private static Pattern mtriple = Pattern.compile("[(]([^, \t]+),([^, \t]+),([^, \t]+)[)][ \t]*:[ \t]*([0-9.,]+)");

    private static final char PREFIX_SET = 'S';

	private static final char PREFIX_LABLE = 'L';

    /// parse Label set from text label
	private static LabelSet readLabel(String vlabel, String buff, final HashMap<String, LabelSet> lset) throws IOException {
		LabelSet res = null;
		switch (vlabel.charAt(0)) {
		case PREFIX_SET:
			res = lset.get(vlabel.substring(1));
			break;
		case PREFIX_LABLE:
			boolean found = false;
			for (LabelSet s : lset.values()) {
				// find the group that contains the label
				if (s.getLabels().contains(vlabel.substring(1))) {
					res = new LabelSet("" + s.getWeight() + " auto_" + s.getId() + " " + vlabel.substring(1));
					found = true;
					continue;
				}
			}
			if (!found) {
				res = new LabelSet("1.0 def " + vlabel.substring(1));
			}
			break;
		default:
			throw new IOException("format error reading on triple prefix label must be L/S:" + vlabel + ": in " + buff);

		}
		return res;
	}


    public static TripleMatchPattern readPattern(String buff, final HashMap<String, LabelSet> lset) throws IOException {
		TripleMatchPattern.TripleMatchPatternBuilder builder = new TripleMatchPattern.TripleMatchPatternBuilder();
        Matcher x = TripleMatchPatternLoader.mtriple.matcher(buff);
		if (x.matches()) {

			TripleMatchOperator plabel = null;
			String vlabel = x.group(1);
			try {
				plabel = TripleMatchOperator.valueOf(vlabel);
			} catch (java.lang.IllegalArgumentException e) {
				// TODO check behavior

			}

			/**
			 * not and operator
			 */
			if (plabel == null) {

				// That can be a Sxxx or Sxxx - Sxxx or Lxxx or Lxxx-Lxxx

				String vlabels[] = vlabel.split("-");

				builder.labelSet(TripleMatchPatternLoader.readLabel(vlabels[0], buff, lset));
				builder.targetLabelSet(vlabels.length > 1 ? readLabel(vlabels[1], buff, lset) : null);
				builder.label(null);

			} else {
				builder.labelSet(null);
				builder.targetLabelSet(null);
				builder.label(plabel);
			}

			builder.head(TripleMatchOperator.valueOf(x.group(2)));
			builder.mod(TripleMatchOperator.valueOf(x.group(3)));
			builder.weight(Double.parseDouble(x.group(4)));
            return builder.build();
		} else {
			System.err
					.println("Error reading pattern for triples at not Label Set or operatir found in pattern:" + buff);
			throw new RuntimeException("Error reading pattern for triples at:" + buff);
		}
	}

}