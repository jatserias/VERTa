package mt.core;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import mt.nlp.Triples;


import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;

/// A simple class for Matching triples
@Slf4j
@Getter
@Setter
public class TriplesMatcher {

    static final String LABEL_PAIR_SEPARATOR = "#";
    public static  final MatchResult NO_MATCH = new MatchResult(0.0, "(o,o,o)");
    // we need to read this weight from the config file
    static final MatchResult COMPLETE_WEIGHT = new MatchResult(1.0, "(x,x,x)");
    static final MatchResult PARTIAL_NO_MOD_WEIGHT = new MatchResult(0.8, "(x,x,o)");
    static final MatchResult PARTIAL_NO_HEAD_WEIGHT = new MatchResult(0.7, "(x,o,x)");
    static final MatchResult PARTIAL_NO_LABEL_WEIGHT = new MatchResult(0.7, "(o,x,x)");

    /// Column name for the triple label
    private String labelColumnName;

    // we also need to incorporate equivalent labels
    // <label label weight>
    /// Column name for the triple head
    private String headColumnName;
    /// label # label matching table
    private HashMap<String, Double> labelMatch;
    // to report counters
    @JsonIgnore
    private MetricActivationCounter counters;

    public TriplesMatcher() {
        setLabelMatch(new HashMap<>());
        counters = new MetricActivationCounter(); 
    }

    /** 
    public TriplesMatcher(String headColumnName, String labelColumnName) {
        setLabelMatch(new HashMap<>());
        this.labelColumnName = labelColumnName;
        this.headColumnName = headColumnName;
        this.setCounters(new MetricActivationCounter());
    }

    public TriplesMatcher(MetricActivationCounter counters, String headColumnName, String labelColumnName) {
        this(headColumnName, labelColumnName);
        this.setCounters(counters);
    }
**/

    static public String getSubLabel(String label) {
        int pos = label.indexOf('_');
        return pos > 0 ? label.substring(0, pos) : label;
    }

    static public boolean isPatternLabel(String label) {
        return label.endsWith("_%");
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
            return PARTIAL_NO_MOD_WEIGHT;
        if (label_match && mod_match)
            return PARTIAL_NO_HEAD_WEIGHT;
        if (head_match && mod_match)
            return PARTIAL_NO_LABEL_WEIGHT;

        return NO_MATCH;
    }

    public MatchResult match(final Triples x, final Triples y, final ISentenceAlignment align) {

        boolean label_match = labelsMatch(x, y);

        // bug head - mod. We reverse the matchings
        boolean mod_match = align.isAligned(x.getTarget() - 1, y.getTarget() - 1);

        boolean head_match = (x.getSource() > 0 && y.getSource() > 0)
                ? align.isAligned(x.getSource() - 1, y.getSource() - 1)
                : (x.getSource() == y.getSource()); // root

        // how we should apply label matching rules
        label_match = label_match || matchRules(x.getLabel(), y.getLabel());

        return matchingScorer(x, y, label_match, head_match, mod_match);
    }

    public boolean labelsMatch(final Triples x, final Triples y) {
        String elabelx = getSubLabel(x.getLabel());
        String elabely = getSubLabel(y.getLabel());

        return (x.getLabel().compareTo(y.getLabel()) == 0)
                || ((isPatternLabel(x.getLabel()) || isPatternLabel(y.getLabel())) && (elabelx.compareTo(elabely) == 0));
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

}
