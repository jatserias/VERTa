package mt;

import lombok.extern.slf4j.Slf4j;
import mt.core.*;
import mt.nlp.Sentence;
import mt.nlp.Triples;
import mt.nlp.Word;
import verta.xml.SentenceSimilarityTripleOverlappingXMLDumper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Vector;

/**
 * compare to sentences by the overlapping of the triplets generated
 * <p>
 * generate a distance matrix i /j then apply the alignment strategy
 * TODO enforce that the target triple is taken only once
 * </p>
 **/
@Slf4j
public class SentenceSimilarityTripleOverlapping extends SentenceSimilarityBase {

    /**
     * Default column names for syntactic dependencies triples
     * Used as default for backward compatible configuration
     **/
    private static final String DEPHEAD_NAME = "DEPHEAD";
    private static final String DEPLABEL_NAME = "DEPLABEL";
    public static boolean FILTER_TOP = false;
    /// USE OLD MATCHING (LREC version)
    static boolean USE_OLD = false;
    /// matching triples
    TriplesMatch tmatch;


    public SentenceSimilarityTripleOverlapping(MetricActivationCounter counters, String configFile) throws IOException {
        this(counters, configFile, mt.nlp.io.FileManager.get_file_content(configFile), DEPHEAD_NAME, DEPLABEL_NAME);
        log.warn("Legacy syntax dependency SentenceSimilarityTripleOverlapping initilization used (please add HEAD and LABEL column's names in your config file)");
    }

    public SentenceSimilarityTripleOverlapping(MetricActivationCounter counters, String configFile, String head_column, String label_column) throws IOException {
        this(counters, configFile, mt.nlp.io.FileManager.get_file_content(configFile), head_column, label_column);
        log.info(String.format("SentenceSimilarityTripleOverlapping  using head from %s and labels from %s", head_column, label_column));
    }

    public SentenceSimilarityTripleOverlapping(MetricActivationCounter counters, String configFile,
                                               BufferedReader config, String head_column, String label_column) throws IOException {
        super(counters);
        tmatch = USE_OLD ? new TriplesMatch(counters, head_column, label_column) : new TripleMatchPattern(counters, head_column, label_column);
        tmatch.load(configFile, config);
    }

    public static Triples MakeTriple(Sentence sentence, Word word, String head_column, String label_column) {
        int target = Integer.parseInt(word.getFeature(Triples.ID_NAME));
        String targetString = word.getFeature(Triples.WORD_NAME);

        String head_string = word.getFeature(head_column);
        int head = head_string.startsWith("_") ? -1 : Integer.parseInt(head_string);
        String sourceString = (head < 1) ? "TOP" : sentence.get(head - 1).getFeature(Triples.WORD_NAME);
        String label = word.getFeature(label_column);
        return new Triples(label, head, target, sourceString, targetString);
    }

    /**
     * Reconstruct/read triples/call parser
     *
     * @param sentence sentence
     * @return a list of triples
     */
    static List<Triples> tripleGenerator(final Sentence sentence, String head_column, String label_column) {
        List<Triples> res = new Vector<>();
        for (Word w : sentence) {
            Triples t = SentenceSimilarityTripleOverlapping.MakeTriple(sentence, w, head_column, label_column);

            if (FILTER_TOP || t.getTarget() >= 1)
                res.add(t);
        }
        return res;
    }

    double compare(final Sentence s1, final Sentence s2, final ISentenceAlignment alignment,
                   PrintStream strace) {
        List<Triples> ts1 = tripleGenerator(s1);
        List<Triples> ts2 = tripleGenerator(s2);
        return compare(ts1, ts2, alignment, strace);
    }

    /**
     * Gets the best triple that match
     **/
    public double findTriple(final Triples n1, final Triples[] s2, final ISentenceAlignment align,
                             PrintStream strace) {

        double res = 0.0;
        Triples btrip = null;

        for (Triples ts2 : s2) {
            double cres = tmatch.match(n1, ts2, align).getScore();
            if (cres > res) {
                btrip = ts2;
                res = cres;
            }
        }

        if (MTsimilarity.DUMP) {
            SentenceSimilarityTripleOverlappingXMLDumper.xml_dump_triples(n1, strace, res, btrip);
        }
        return res;
    }

    private double compare(final List<Triples> ts1, final List<Triples> ts2,
                           final ISentenceAlignment alignment, PrintStream strace) {
        // That may no be symmetric if align is not symmetric
        double res = 0.0;

        for (Triples n1 : ts1.toArray(new Triples[0])) {

            res += (findTriple(n1, ts2.toArray(new Triples[0]), alignment, strace));

        }
        //
        return res;
    }

    public List<Triples> tripleGenerator(final Sentence sentence) {
        return tripleGenerator(sentence, tmatch.getHeadColumnName(), tmatch.getLabelColumnName());
    }

    @Override
    public SimilarityResult similarity(final Sentence proposedSentence, final Sentence referenceSentence,
                                       final ISentenceAlignment word_alignment, PrintStream strace) {
        List<Triples> ts1 = tripleGenerator(proposedSentence);
        List<Triples> ts2 = tripleGenerator(referenceSentence);

        ISentenceAlignment triples_align = new AlignmentImplSingle(ts1.size(), ts2.size());
        double prec = calculate_metric(word_alignment, strace, ts1, ts2, triples_align);

        ISentenceAlignment triples_align_rev = new AlignmentImplSingle(ts2.size(), ts1.size());
        double recall = calculate_metric(word_alignment.revert(), strace, ts2, ts1, triples_align_rev);

        return new SimilarityResult(prec, recall);
    }

    private double calculate_metric(final ISentenceAlignment word_alignment, PrintStream strace,
                                    List<Triples> triples_source,
                                    List<Triples> triples_target,
                                    ISentenceAlignment triples_align) {

        DistanceMatrix distances = createDist(triples_source, triples_target, word_alignment);

        // TODO Normalization is going to hell
        double sum = 0.0;
        for (Triples t : triples_source) {
            sum += tmatch.getWeight(t.getLabel());
        }
        return sum > 0 ? INsimilarity(triples_align, triples_source, triples_target, distances, strace) / sum : 0.0;
    }

    /// new internal similarity function
    public double INsimilarity(
            ISentenceAlignment triple_alignment,
            final List<Triples> proposedSentence,
            final List<Triples> referenceSentence,
            DistanceMatrix distances,
            PrintStream strace) {

        new AlignmentBuilderBestMatch().build(triple_alignment, distances);


        double res = 0;

        // source alignment
        int i_align = 0;
        for (int i_al : triple_alignment.getAlignment()) {
            if (i_al >= 0)
                res += distances.getDistance(i_align, i_al);
            ++i_align;
        }

        if (MTsimilarity.DUMP) {
            SentenceSimilarityTripleOverlappingXMLDumper.xml_dump_alignment(true, tmatch, triple_alignment, proposedSentence, referenceSentence, distances, strace, i_align);
            SentenceSimilarityTripleOverlappingXMLDumper.xml_dump_alignment(false, tmatch, triple_alignment, proposedSentence, referenceSentence, distances, strace, i_align);
        }

        // we need to normalize
        return res; // al.length >0 ? res / al.length : 0.0;
    }


    protected DistanceMatrix createDist(final List<Triples> ts1, final List<Triples> ts2,
                                        final ISentenceAlignment word_alignment) {
        DistanceMatrix res = new DistanceMatrix(ts1.size(), ts2.size());

        int i = 0;
        for (Triples n1 : ts1) {
            int j = 0;
            for (Triples n2 : ts2) {
                // distance inverse the indexes while align keeps 2 different representation

                MatchResult tres = tmatch.match(n1, n2, word_alignment);
                res.setDistance(i, j, tres.getScore(), tres.getProv());
                ++j;
            }
            ++i;
        }
        return res;
    }

    @Override
    public void dump(PrintStream strace) {
        strace.print("<metric name='dependency triples'/>");
    }

}
