package mt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Vector;

import mt.core.AlignmentBuilderBestMatch;
import mt.core.AlignmentImpl;
import mt.core.DistanceMatrix;
import mt.core.MatchResult;
import mt.core.MetricActivationCounter;
import mt.core.SentenceAlignment;
import mt.core.SentenceMetric;
import mt.core.SentenceSimilarityBase;
import mt.core.SimilarityResult;
import mt.core.TripleMatchPattern;
import mt.core.TriplesMatch;
import mt.nlp.Sentence;
import mt.nlp.Triples;
import mt.nlp.Word;
import verta.xml.SentenceSimilarityTripleOverlappingXMLDumper;

/**
 * 
 * 
 * compare to sentences by the overlapping of the triplets generated
 * 
 * generate a distance metrix i /j then apply the alignment strategy
 * 
 * @TODO enforce that the target triple is taken only once
 * 
 **/
public class SentenceSimilarityTripleOverlapping extends SentenceSimilarityBase implements SentenceMetric {

	public static boolean FILTER_TOP = false;

	/// USE OLD MATCHING (LREC version)
	static boolean USE_OLD = false;

	/// matching triples
	TriplesMatch tmatch;

	/**
	 * Default column names for syntatic dependences triples
	 * Used as default for backward compatible configuration
	 */
	private static final String DEPHEAD_NAME = "DEPHEAD";
	private  static final String DEPLABEL_NAME = "DEPLABEL";

	/**
	 * we should parametrice
	 * 
	 * @param configfile
	 * @throws IOException
	 */
	public SentenceSimilarityTripleOverlapping(MetricActivationCounter counters, String configFile) throws IOException {
			this(counters, configFile, mt.nlp.io.FileManager.get_file_content(configFile));
	}
		

	public SentenceSimilarityTripleOverlapping(MetricActivationCounter counters, String configFile,
			BufferedReader config) throws IOException {
		super(counters);
		tmatch = USE_OLD ? new TriplesMatch(counters, DEPHEAD_NAME, DEPHEAD_NAME) : new TripleMatchPattern(counters, DEPHEAD_NAME, DEPHEAD_NAME);
		tmatch.load(configFile, config);
	}

	double compare(boolean reversed, final Sentence s1, final Sentence s2, final SentenceAlignment alignment,
			PrintStream strace) {
		List<Triples> ts1 = tripleGenerator(s1);
		List<Triples> ts2 = tripleGenerator(s2);
		return compare(reversed, ts1, ts2, alignment, strace);
	}

	/**
	 * Gets the best triple that match
	 * 
	 * @param reversed
	 * @param n1
	 * @param s2
	 * @param align
	 * @param strace
	 * @return
	 */
	public double findTriple(boolean reversed, final Triples n1, final Triples[] s2, final SentenceAlignment align,
			PrintStream strace) {

		double res = 0.0;
		Triples btrip = null;

		for (Triples ts2 : s2) {
			double cres = tmatch.match(reversed, n1, ts2, align).score;
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

	private double compare(boolean reversed, final List<Triples> ts1, final List<Triples> ts2,
			final SentenceAlignment alignment, PrintStream strace) {
		// That may no be symmetric if align is not symmetric
		double res = 0.0;

		for (Triples n1 : ts1.toArray(new Triples[0])) {

			res += (findTriple(reversed, n1, ts2.toArray(new Triples[0]), alignment, strace));

		}
		//
		return res;
	}


	public static Triples MakeTriple(Sentence sentence, Word word, String head_column, String label_column) {
		int target = Integer.parseInt(word.getFeature(Triples.ID_NAME));
		String targetString = word.getFeature(Triples.WORD_NAME);

		String head = word.getFeature(head_column);
		int source = head.startsWith("_") ? -1 : Integer.parseInt(head);
		String sourceString = (source < 1) ? "TOP" : sentence.get(source - 1).getFeature(Triples.WORD_NAME);
		String label = word.getFeature(label_column);
		return new Triples(label, source, target, sourceString,targetString);
	}
	
	public static Triples MakeTriple(final Sentence sentence, final Word word) throws NumberFormatException {
		return MakeTriple(sentence, word, SentenceSimilarityTripleOverlapping.DEPHEAD_NAME, SentenceSimilarityTripleOverlapping.DEPLABEL_NAME);
	}
	
	/**
	 * Reconstruct/read triples/call parser
	 * 
	 * @param s1 sentence
	 * @return a list of triples
	 */
	static List<Triples> tripleGenerator(final Sentence s1) {
		List<Triples> res = new Vector<Triples>();
		for (Word w : s1) {
			Triples t = SentenceSimilarityTripleOverlapping.MakeTriple(s1, w);

			if (FILTER_TOP || t.getTarget() >= 1)
				res.add(t);
		}
		return res;
	}

	double getMax() {
		return 0;
	}

	@Override
	public SimilarityResult similarity(final Sentence proposedSentence, final Sentence referenceSentence,
			final SentenceAlignment word_alignment, PrintStream strace) {
		List<Triples> ts1 = tripleGenerator(proposedSentence);
		List<Triples> ts2 = tripleGenerator(referenceSentence);
		SentenceAlignment triples_align = new AlignmentImpl(ts1.size(), ts2.size());

		double prec = calculate_metric(false, word_alignment, strace, ts1, ts2, triples_align);
		double recall = calculate_metric(true, word_alignment, strace, ts2, ts1, triples_align);
		
		return new SimilarityResult(prec, recall);
	}

	private double calculate_metric(boolean reversed, final SentenceAlignment word_alignment, PrintStream strace, 
			List<Triples> ts1,
			List<Triples> ts2, 
			SentenceAlignment triples_align) {
		DistanceMatrix d = createDist(reversed, ts1, ts2, word_alignment);

		// TODO Normalization is going to hell
		double sum = 0.0;
		for (Triples t : ts1) {
			sum += tmatch.getWeight(t.label);
		}
		return sum > 0 ? INsimilarity(reversed, triples_align, ts1, ts2, d, strace) / sum : 0.0;
	}

	/// new internal similarity function
	public double INsimilarity(boolean reversed, SentenceAlignment triple_alignment, final List<Triples> proposedSentence,
			final List<Triples> referenceSentence, DistanceMatrix d, PrintStream strace) {

		new AlignmentBuilderBestMatch().build(reversed, triple_alignment, d);

		double res = 0;

		int i_align = 0;
		for (int i_al : triple_alignment.getAlignment(reversed)) {
			if (i_al >= 0)
				res += d.getDistance(reversed, i_align, i_al);
			++i_align;
		}

		if (MTsimilarity.DUMP) {
			SentenceSimilarityTripleOverlappingXMLDumper.xml_dump_alignment(reversed, tmatch, triple_alignment, proposedSentence, referenceSentence, d, strace, i_align);
		}

		// we need to normalize
		return res; // al.length >0 ? res / al.length : 0.0;
	}


	protected DistanceMatrix createDist(boolean reversed, final List<Triples> ts1, final List<Triples> ts2,
			final SentenceAlignment word_alignment) {
		DistanceMatrix res = new DistanceMatrix(ts1.size(), ts2.size());

		int i = 0;
		for (Triples n1 : ts1) {
			int j = 0;
			for (Triples n2 : ts2) {
				// distance inverse the indexes while align keeps 2 different representation
				if(reversed) {
					 MatchResult tres = tmatch.match(true, n1, n2, word_alignment);
					 res.setDistance(true, j, i, tres.score, tres.prov);}
				 else {
					 MatchResult tres =tmatch.match(false, n1, n2, word_alignment);
				 	 res.setDistance(false, i, j, tres.score, tres.prov);	}
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
