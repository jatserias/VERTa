package mt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Logger;

import com.martiansoftware.jsap.JSAP;

import mt.core.MetricResult;
import mt.core.Verta;
import mt.nlp.Segment;
import mt.nlp.Sentence;
import mt.nlp.io.CONLLformat;
import mt.nlp.io.ReaderCONLL;
import verta.wn.WordNetAPI;
import verta.wn.WordNetFactory;
import verta.xml.MTmetricXMLDumper;

/**
 * 
 * input: Multitag file and references files, and metric configuration files and
 * a name for the experiment
 * 
 * output: - a browsable XML in folder <BASEFOLDER> - a cvs tab separated per
 * segment (max F1 among the references) <precision>\t<recall>\t<f1> - a file
 * per metric module with all the information needed to compute the metric
 * 
 * each module outputs mod(segment hyp x segment ref) -> P R F1 if it is a
 * weighted module of several companents the module must putput mod(hyp x ref)
 * c1 c2 c3 ... cn
 * 
 * 
 * convertion form old conll format
 * 
 * grep -v '%%#SEG' Sentences_with_issues_hyp.conll | awk 'BEGIN {S=2;print
 * "%%#SEG\t1";} /^[ ]*$/ { print "\n%%#SEG\t"S;S+S+1;next} { print
 * $1"\t"$2"\t"$4"\t"$4"\t"$3"\t-\t-\t-\t"$8"\t"$7}' >
 * Sentences_with_issues_Hyp.tag
 * 
 */
public class MTsimilarity {

	private static Logger LOGGER = Logger.getLogger(MTsimilarity.class.getName());

	public Verta verta;

	public static final String BASEFOLDER = "exp/";
	private static final String precrecallfile = "precrec.txt";

	public static boolean DUMP;

	public static void usage() {
		System.err.println("Usage: Three parameters needed");
		System.err.println("- Metric config filename");
		System.err.println("- Metric config filename");
		System.err.println("- Experiment Name");
		System.err.println("- proposed filename in conll format");
		System.err.println("- references filenames in conll format");
		System.err.println("plus an optional parameter: the name of the experiment");
	}

	public MTsimilarity(String configFilename, CONLLformat fmt, WordNetAPI wn) {
		this.verta = new Verta(configFilename, wn);
	}

	public MTsimilarity(String configFilename, BufferedReader buffer, CONLLformat fmt, WordNetAPI wn) {
		this.verta = new Verta(configFilename, buffer, wn);
	}

	public static void main(String[] args) throws Exception {

		// Read arguments
		try {
			com.martiansoftware.jsap.FlaggedOption mopt = new com.martiansoftware.jsap.FlaggedOption("hypothesis",
					JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, 'h', JSAP.NO_LONGFLAG,
					"Proposed-hypotesis translation files");
			mopt.setList(true);
			mopt.setListSeparator(',');
			com.martiansoftware.jsap.FlaggedOption topt = new com.martiansoftware.jsap.FlaggedOption("references",
					JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, 'r', JSAP.NO_LONGFLAG,
					"Reference Translations files");
			topt.setList(true);
			topt.setListSeparator(',');

			final com.martiansoftware.jsap.SimpleJSAP jsap = new com.martiansoftware.jsap.SimpleJSAP(
					MTsimilarity.class.getName(), "VERTa metric  ",
					new com.martiansoftware.jsap.Parameter[] {
							new com.martiansoftware.jsap.UnflaggedOption("conf", JSAP.STRING_PARSER, JSAP.NO_DEFAULT,
									JSAP.REQUIRED, JSAP.NOT_GREEDY, "metric confic file"),
							new com.martiansoftware.jsap.UnflaggedOption("exp", JSAP.STRING_PARSER, JSAP.NO_DEFAULT,
									JSAP.REQUIRED, JSAP.NOT_GREEDY, "experiment name"),
							topt, mopt,
							new com.martiansoftware.jsap.Switch("xml", 'x', "xml", "Generate xml trace files"),
							new com.martiansoftware.jsap.Switch("punc", 'p', "punc", "Filter punctuation"),
							new com.martiansoftware.jsap.Switch("top", 't', "top", "Include TOP dependencies"),
							new com.martiansoftware.jsap.Switch("old", 'o', "old", "try running OLD triples module"),
							new com.martiansoftware.jsap.UnflaggedOption("lang", JSAP.STRING_PARSER, "en",
									JSAP.NOT_REQUIRED, JSAP.NOT_GREEDY, "traget language") });

			final com.martiansoftware.jsap.JSAPResult jsapResult = jsap.parse(args);
			if (jsap.messagePrinted())
				return;

			String metricConfigFile = jsapResult.getString("conf");
			String hypFilenames[] = jsapResult.getStringArray("hypothesis");
			String experimentName = jsapResult.getString("exp");
			String refFilenames[] = jsapResult.getStringArray("references");
			String language = jsapResult.getString("lang");
			DUMP = jsapResult.getBoolean("xml", false);

			LOGGER.warning("XML trace" + (DUMP ? "Activated" : "Deactivated"));
			MTmetricXMLDumper.xml_copy_files();
			long tStart = System.currentTimeMillis();

			// Hack to format numbers in Spanish format (some of my machine has locale set
			// to English)
			Locale local = new Locale("es", "ES");
			Locale.setDefault(local);

			NumberFormat nf = new DecimalFormat("#,###,##0.0000000");

			int nSystem = 0;

			SentenceSimilarityTripleOverlapping.USE_OLD = jsapResult.getBoolean("old", false);
			SentenceSimilarityTripleOverlapping.FILTER_TOP = !jsapResult.getBoolean("top", false);

			WordNetAPI wn = WordNetFactory.getWordNet(language, "/usr/local/wordnets");
			CONLLformat fmt = new CONLLformat("conf/conll08.fmt");
			MTsimilarity mt = new MTsimilarity(metricConfigFile, fmt, wn);
			mt.verta.setFilter(jsapResult.getBoolean("punc", false));
			mt.verta.getTracer().DUMP = DUMP;

			PrintStream precrec = null;

			// foreach hyp file (e.g. system)
			for (String hypFilename : hypFilenames) {
				++nSystem;

				// Dump configuration
				PrintStream trace = null;
				precrec = new PrintStream(BASEFOLDER + experimentName + "_" + nSystem + "_" + precrecallfile, "UTF-8");

				trace = MTmetricXMLDumper.start_xml_dump(metricConfigFile, experimentName, refFilenames, nSystem,
						hypFilename, trace);

				// @TODO we should generalize to Sentence Metric as NGram is not Word Metric
				// load similarityFunction class by name
				// create a WordSimilarity function

				MTmetricXMLDumper.xml_dump_configuration(mt, trace);

				// TEST reading just one sentence
				BufferedReader proposedFile = new BufferedReader(new FileReader(hypFilename));
				BufferedReader referenceFiles[] = new BufferedReader[refFilenames.length];
				int j = 0;
				for (String refFilename : refFilenames) {
					referenceFiles[j++] = new BufferedReader(new FileReader(refFilename));
				}

				int nseg = 1;
				// read sentences CONLL format (Establish the possible features names WORD LEMA
				// ...)
				Segment proposedSeg;
				Sentence proposedSentence;
				try {
					proposedSeg = ReaderCONLL.readSegment(proposedFile, fmt);
					proposedSentence = proposedSeg.toSentence();
				} catch (Exception e) {
					e.printStackTrace();
					proposedSentence = null;
					proposedSeg = null;
				}

				int nsen = 1;
				// for every proposed sentence
				while (proposedSentence != null) {

					PrintStream gtrace = null;
					PrintStream strace = null;

					gtrace = MTmetricXMLDumper.xml_dump_sentence(experimentName, nSystem, mt, nseg, proposedSentence,
							nsen, gtrace);

					MetricResult MAXRes = null;

					int nref = 0;
					/**
					 * for every reference we should create a different file dump??
					 */
					for (BufferedReader referenceFile : referenceFiles) {

						Segment referenceSeg = null;
						Sentence referenceSentence = null;
						try {
							referenceSeg = ReaderCONLL.readSegment(referenceFile, fmt);
							referenceSentence = referenceSeg.toSentence();
						} catch (Exception e) {
							e.printStackTrace();
						}

						// it should be always a "sentence" per segment
						if (referenceSentence != null) {

							strace = MTmetricXMLDumper.xml_dump_sentence_ref(experimentName, nSystem, mt, nseg,
									proposedSentence, nsen, gtrace, strace, nref, referenceSentence);
							nref++;

							// Read the metric configuration: featureValue SimiliarityFunction weigh
							mt.verta.getTracer().strace = strace;
							MetricResult res = mt.verta.similarity(referenceSentence, proposedSentence); // , DUMP,
							// strace);

							MTmetricXMLDumper.xml_similarity_dump(gtrace, res);

							// get the better F1
							if (MAXRes == null || res.getWF1() > MAXRes.getWF1()) {
								MAXRes = res;
							}

							nsen++;
						}

					} // for references file

					// final result
					MTmetricXMLDumper.xml_dump_global_results(experimentName, nSystem, trace, nseg, nsen, gtrace,
							MAXRes);

					// VERTA
					MAXRes.textdump(precrec, nf);

					// next pair
					try {
						proposedSeg = ReaderCONLL.readSegment(proposedFile, fmt);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					nseg++;
					nsen = 1;

					if (proposedSeg != null) {
						MTmetricXMLDumper.xml_add_sentence_link(experimentName, nSystem, nseg, gtrace);
						proposedSentence = proposedSeg.toSentence();
						// referenceSentence = referenceSeg.toSentence();
					} else
						proposedSentence = null;

					MTmetricXMLDumper.xml_close_experiment(gtrace, strace);

					if (nseg % 10 == 0)
						LOGGER.warning("Processed sys: " + nSystem + " seg:" + nseg + " ...  milsec: "
								+ (System.currentTimeMillis() - tStart));

				}
				// proposed sentence

				MTmetricXMLDumper.xml_dump_statistics(mt, trace);

				precrec.close();
			}

			mt.verta.getCounters().dump();
			LOGGER.warning("TOTAL TIME milsec: " + (System.currentTimeMillis() - tStart));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
