package mt.core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import mt.SentenceSimilarityTripleOverlapping;
import mt.nlp.Triples;

/// A simple class for Matching triples
public class TriplesMatch {
	
	protected static Logger LOGGER = Logger.getLogger(TriplesMatch.class.getName());
	
	private static final String LABEL_PAIR_SEPARATOR = "#";
	// we need to read this weight from the config file
	static MatchResult COMPLETE_WEIGHT = new MatchResult(1.0, "(x,x,x)");
	static MatchResult PARTIAL_NOMOD_WEIGHT = new MatchResult(0.8, "(x,x,o)");
	static MatchResult PARTIAL_NOHEAD_WEIGHT = new MatchResult(0.7, "(x,o,x)");
	static MatchResult PARTIAL_NOLABEL_WEIGHT = new MatchResult(0.7, "(o,x,x)");
	public static MatchResult NOMATCH = new MatchResult(0.0, "(o,o,o)");

	/// label # label matching table
	private HashMap<String, Double> labelMatch;

	// we also need to incorporate equivalent labels
	// label label weight

	// to report counters
	private MetricActivationCounter counters;

	/// Column name for the triple label
	String label_column_name;
	
	/// Column name for the triple head
	String head_column_name;
	
	public TriplesMatch(String head_column_name, String label_column_name) {
		setLabelMatch(new HashMap<String, Double>());
		this.label_column_name = label_column_name;
		this.head_column_name = head_column_name;
		this.setCounters(new MetricActivationCounter());
	}
		
	public TriplesMatch(MetricActivationCounter counters, String head_column_name, String label_column_name) {
		this(head_column_name, label_column_name);
		this.setCounters(counters);
	}

	public TriplesMatch(String filename, MetricActivationCounter counters, String head_column_name, String label_column_name) throws FileNotFoundException, IOException {
		this(counters, head_column_name, label_column_name);
		load(filename);
	}
		
	protected void load(String filename) throws FileNotFoundException, IOException {
		BufferedReader config = null;
		try {
			config = new BufferedReader(new FileReader(filename));
			load(filename, config);
		} catch (Exception e) {
			System.err.println("ERROR can not open/find file >" + filename + "<");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void load(String filename, BufferedReader config)  throws IOException {
		String buff = null;
		try {

			// read weights
			while ((buff = config.readLine()) != null && buff.trim().startsWith(LABEL_PAIR_SEPARATOR));
			
			if (buff == null) {
				System.err.println("Format ERROR on the triple config file >" + filename + "<");
				System.err.println("EMPTY FILE");
				System.exit(-1);
			}

			String[] wbuff = buff.split("[\t]");
			int i = 0;
			COMPLETE_WEIGHT.score = Double.parseDouble(wbuff[i++]);
			PARTIAL_NOMOD_WEIGHT.score = Double.parseDouble(wbuff[i++]);
			PARTIAL_NOHEAD_WEIGHT.score = Double.parseDouble(wbuff[i++]);
			PARTIAL_NOLABEL_WEIGHT.score = Double.parseDouble(wbuff[i++]);

			/// Read rules
			while ((buff = config.readLine()) != null) {
				if (!buff.trim().startsWith(LABEL_PAIR_SEPARATOR)) {
					String[] label = buff.split("[\t]");
					getLabelMatch().put(label[0] + LABEL_PAIR_SEPARATOR + label[1], Double.parseDouble(label[2]));
				}
			}
		} catch (Exception e) {
			System.err.println("Error reading triplet config file:" + buff);
			e.printStackTrace();
		}
	}

	public double getWeight(String label) {
		return 1.0;
	}

	/// returns the score given the matching pattern
	public MatchResult matchingScorer(final Triples x, final Triples y, boolean label_match, boolean head_match,
			boolean mod_match) {
		// This is the part to be customized
		if (label_match && head_match && mod_match)
			return COMPLETE_WEIGHT;
		if (label_match && head_match)
			return PARTIAL_NOMOD_WEIGHT;
		if (label_match && mod_match)
			return PARTIAL_NOHEAD_WEIGHT;
		if (head_match && mod_match)
			return PARTIAL_NOLABEL_WEIGHT;

		return NOMATCH;
	}

	@Deprecated
	public MatchResult match(boolean reversed, final Triples x, final Triples y, final SentenceAlignment align) {

		boolean label_match = labelsMatch(x, y);

		// bug head - mod. We reverse the matchings
		boolean mod_match = align.isAligned(reversed, x.getTarget() - 1, y.getTarget() - 1);

		boolean head_match = (x.getSource() > 0 && y.getSource() > 0)
				? align.isAligned(reversed, x.getSource() - 1, y.getSource() - 1)
				: (x.getSource() == y.getSource()); // root

		// how we should apply label matching rules
		label_match = label_match || matchRules(x.label, y.label);

		return matchingScorer(x, y, label_match, head_match, mod_match);
	}

	public boolean labelsMatch(final Triples x, final Triples y) {
		String elabelx = getSubLabel(x.label);
		String elabely = getSubLabel(y.label);

		return (x.label.compareTo(y.label) == 0)
				|| ((isPatternLabel(x.label) || isPatternLabel(y.label)) && (elabelx.compareTo(elabely) == 0));
	}

	/**
	 * TODO this function seems to be call but labelMatch is empty (for the new dep
	 * match version)
	 */
	public boolean matchRules(String label, String label2) {
		// extended
		String elabel = getSubLabel(label);
		String elabel2 = getSubLabel(label2);

		return
		// direct match
		(getLabelMatch().get(label + LABEL_PAIR_SEPARATOR + label2) != null
				|| getLabelMatch().get(label2 + LABEL_PAIR_SEPARATOR + label) != null) ||
		// pattern label match
				((isPatternLabel(label) || isPatternLabel(label2))
						&& (getLabelMatch().get(elabel + LABEL_PAIR_SEPARATOR + elabel2) != null
								|| getLabelMatch().get(elabel2 + LABEL_PAIR_SEPARATOR + elabel) != null));
	}

	static public String getSubLabel(String label) {
		int pos = label.indexOf('_');
		return pos > 0 ? label.substring(0, pos) : label;
	}

	static public boolean isPatternLabel(String label) {
		return label.endsWith("_%");
	}

	public MetricActivationCounter getCounters() {
		return counters;
	}

	public void setCounters(MetricActivationCounter counters) {
		this.counters = counters;
	}

	public HashMap<String, Double> getLabelMatch() {
		return labelMatch;
	}

	public void setLabelMatch(HashMap<String, Double> labelMatch) {
		this.labelMatch = labelMatch;
	}


}
