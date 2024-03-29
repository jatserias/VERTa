package mt.core;

import lombok.extern.slf4j.Slf4j;
import mt.nlp.Triples;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * new support for:
 * Defining sets of labels (with associate weights)
 * eg Set1={} with weight X Set2 with weight={} ...
 * Define patterns of matching of triples (pattens to be applied in order)
 * =: same vale s&lt;NUM&gt;: label on set *: any
 * (S,H,T)
 * COMPLETE_WEIGHT PARTIAL_NOMOD_WEIGHT PARTIAL_NOHEAD_WEIGHT
 * PARTIAL_NOLABEL_WEIGHT 1.0 0.8 0.7 0.7
 * e.g. (*,S1,*) : NUMBER
 * (=,S1,=) : Number
 * Extension to simplify preposition
 * deplabel_%
 **/

@Slf4j
public class TripleMatchPattern extends TriplesMatch {

    private static final String COMMENT = "#";

    /// Groups of labels with associated weights. A label can be a pattern: dep_%
    HashMap<String, LabelSet> groups;
    /// a collection of patterns to try
    Collection<Tpattern> lp;

    public TripleMatchPattern(MetricActivationCounter counters, String head_column_name, String label_column_name) {
        super(counters, head_column_name, label_column_name);
        log.info("Initilizing patterns");
        // TODO load the rest of parameters
        groups = new HashMap<>();
        lp = new ArrayList<>();
    }

    private void readSets(BufferedReader fconf) throws IOException {
        String buff;
        while ((buff = fconf.readLine()) != null && buff.startsWith("##%SETS")) ;
        while ((buff = fconf.readLine()) != null && !buff.startsWith("%%#PATTERNS")) {
            if (!buff.startsWith(COMMENT)) {
                LabelSet s = new LabelSet(buff);
                groups.put(s.id, s);
            }
        }

    }

    private void readPatterns(BufferedReader fconf) throws IOException {
        String buff;
        while ((buff = fconf.readLine()) != null) {
            if (!buff.startsWith(COMMENT)) {
                if (buff.trim().length() > 0) {
                    Tpattern s = new Tpattern(buff, this.groups);
                    lp.add(s);
                }
            }
        }


    }

    @Override
    public void load(String filename, BufferedReader config) throws IOException {
        readSets(config);
        readPatterns(config);
    }

    @Override
    public MatchResult matchingScorer(final Triples x, final Triples y, boolean label_match, boolean source_match,
                                      boolean target_match) {
        // we should combine weight
        // Pw * Lw * Sw

        for (Tpattern p : lp) {

            if (p.match(x, y, label_match, source_match, target_match)) {

                getCounters().increase(this.getClass().getName() + "[" + p + "]", 1);
                return new MatchResult(
                        p.getWeight() * (p.getLabelSet() == null ? getWeight(x.getLabel()) : p.getLabelSet().w), p);

            }

        }
        // Should we return 0 or -1 ??
        return TriplesMatch.NO_MATCH;

    }

    /**
     * getting the weigh of a label or set of labels
     */
    public double getWeight(String label) {
        for (LabelSet l : groups.values()) {
            if (l.contains(label.toLowerCase()))
                return l.w;
        }
        // try extended pattern
        int pos = label.indexOf('_');
        if (pos > 0) {
            String mlabel = label.substring(0, pos) + "_%";
            for (LabelSet l : groups.values()) {
                if (l.contains(mlabel.toLowerCase()))
                    return l.w;
            }
        }

        return 1.0;
    }

    public enum OPERATOR {
        X, O
    }

}
