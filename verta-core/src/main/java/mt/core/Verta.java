package mt.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import mt.MTsimilarity;
import mt.WordMetric;
import mt.nlp.Sentence;
import mt.nlp.Word;
import verta.wn.WordNetAPI;
import verta.xml.AlignmentImplXMlDumper;
import verta.xml.VertaXMLDumper;

public class Verta {

	private static Logger LOGGER = Logger.getLogger(Verta.class.getName());
	
	public boolean FILTER_PUNCTUATION = false;
	
	/// Word Metrics
	public List<WordMetric> wms;

	/// Sentence Level metrics
	public List<WeightedSentenceMetric> sm;

	/// WordNet API
	public WordNetAPI wn;
	
	private VertaXMLDumper tracer;
	
	private MetricActivationCounter counters;
	
	public Verta(String configFilename, WordNetAPI wn) {
		setTracer(new VertaXMLDumper()); 
		wms = new Vector<WordMetric>();
		sm = new Vector<WeightedSentenceMetric>();
		setCounters(new MetricActivationCounter());
		this.wn = wn;
		load(configFilename);
	}
	
	public Verta(String configFilename, BufferedReader buffer, WordNetAPI wn) {
		setTracer(new VertaXMLDumper()); 
		wms = new Vector<WordMetric>();
		sm = new Vector<WeightedSentenceMetric>();
		setCounters(new MetricActivationCounter());
		this.wn = wn;
		load(configFilename, buffer);
	}
	
	public void load(String configFilename) {
		BufferedReader config = null;
		try {
			config = new BufferedReader(new FileReader(configFilename));
		} catch (Exception e) {
			LOGGER.severe("ERROR can not open/find file >" + configFilename + "<");
			e.printStackTrace();
			System.exit(-1);
		}
		load(configFilename, config);
	}

	public void load(String configFilename, BufferedReader config) {
		String buff;
		try {

			// read weights
			while ((buff = config.readLine()) != null) {
				if (!buff.trim().startsWith("#") && buff.trim().length() > 0) {

					if (!buff.startsWith("GROUP")) {
						LOGGER.severe("Format ERROR on the metric config file >" + configFilename + "<");
						LOGGER.severe("At line:" + buff);
						LOGGER.severe("GROUP head expected");
					} else {
						// GROUP HEAD: GROUP <tab> ID <tab> WEIGHT <tab> CLASSNAME
						String line[] = buff.split("\t");
						if (line.length < 5) {
							LOGGER.severe("Format ERROR on the metric config file >" + configFilename + "<");
						}
						LOGGER.info("READING GROUP>" + buff + "<");
						String classname = line[4];
						double groupWeight = Double.parseDouble(line[3]);

						// int id
						String name = line[2];

						if (classname.compareTo("mt.WordMetric") == 0) {
							// add wordmetric group
							wms.add(new WordMetric(name, config, groupWeight, configFilename, getCounters(), this.wn));
						} else {
							// instantiate class
							sm.add(new WeightedSentenceMetric(name, groupWeight, SentenceMetricBuilder
									.instantiateSentenceMetric(classname, line, getCounters(), this.wn)));
						}
					}

				}
			}
		} catch (Exception e) {
			LOGGER.severe("Format Error in Metric configuration file");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void setFilter(boolean filter) {
		FILTER_PUNCTUATION = filter;	
	}
	
	/**
	 * 
	 * returns MetricResult P / R /F
	 * 
	 */
	public MetricResult similarity(final Sentence referenceSentence, final Sentence proposedSentence) {
		
		getTracer().start_lexical_metrics();
		
		/// Applies word-alignment metrics
		SentenceAlignment lex_align = null;
		SentenceAlignment lex_align_rev = null;
		
		/// store the partial results
		MetricResult tres = new MetricResult();

		/**
		 * for w X w apply all word metrics
		 * 
		 * As a side result a matrix of distance (tres.dist) and an alignment between
		 * the sentences is build
		 */
		int nsim = 0;
		for (WordMetric iwm : wms) {
			// Return also dist matrix (internally uses align)

			AlignmentBuilder builder = new AlignmentBuilderBestMatch();
			
			// calculate all distances
			DistanceMatrix dist = create_word_distance_matrix(iwm, false, proposedSentence, referenceSentence);
			AlignmentImpl align = new AlignmentImpl(proposedSentence.size(), referenceSentence.size());
			// TODO configure alignment strategy
			builder.build(false, align, dist);
			double prec = calculate_similarity_for_alignment(dist, align, false, proposedSentence, this.FILTER_PUNCTUATION);
			
			
			DistanceMatrix dist_rev = create_word_distance_matrix(iwm, true,  referenceSentence, proposedSentence);
			// This was the previous heuristic: AlignmentImpl align_rev = align;
			builder.build(true, align, dist_rev);
			double rec = calculate_similarity_for_alignment(dist_rev, align, true, referenceSentence, this.FILTER_PUNCTUATION);
			
		
			// dump the alignment
			if (MTsimilarity.DUMP) {
				AlignmentImplXMlDumper.dump(align, getTracer().strace);
			}

			tres.add(iwm.getName(), iwm.getWeight(), prec, rec);

			getTracer().xml_dump_distances(referenceSentence, proposedSentence, iwm);

			// use first word distance to alignments
			if(nsim==0){
				lex_align = align;
			}
			++nsim;
		}

		getTracer().end_lexical_metrics();

		/***
		 * Call sentence level metrics using align[] for word metrics
		 */

		getTracer().star_sentence_metrics();

		for (WeightedSentenceMetric iwm : sm) {
			SimilarityResult res = iwm.similarity(proposedSentence, referenceSentence, lex_align, getTracer().strace);
			double prec = res.getPrec();
			double rec = res.getRec();

			tres.add(iwm.getName(), iwm.getWeight(), prec, rec);

		}
		
		getTracer().end_sentence_metrics(tres);
		
		return tres;
	}

	public VertaXMLDumper getTracer() {
		return tracer;
	}

	public void setTracer(VertaXMLDumper tracer) {
		this.tracer = tracer;
	}

	public MetricActivationCounter getCounters() {
		return counters;
	}

	public void setCounters(MetricActivationCounter counters) {
		this.counters = counters;
	}

	// Word sentence metric
 
	/**
	 * 
	 * adds up all similarity for all aligned words that are not filtered (e,g, punctuation)
	 * 
	 * @param dist
	 * @param a
	 * @param reversed
	 * @param proposedSentence
	 * @param filter
	 * @return
	 */
	static double calculate_similarity_for_alignment(DistanceMatrix dist, SentenceAlignment a, final boolean reversed,
			final Sentence proposedSentence, boolean filter) {
		// calculate scores given the selected alignment
		int nwords = 0;

		double res = 0;
		int i = 0;
		for (int i_al: a.getAlignment(reversed)) {
			if (! filter || !WordFilter.filter(proposedSentence.get(i))) {
				if (i_al >= 0)
					res += dist.getDistance(reversed, i, i_al);
				nwords++;
			}
			i++;
		}

		return res / nwords++;
	}

	static DistanceMatrix create_word_distance_matrix(WordMetric iwm,  final boolean reversed,
			final Sentence proposedSentence, final Sentence targetSentence) {
		
		DistanceMatrix dist = reversed ? new DistanceMatrix(targetSentence, proposedSentence) : new DistanceMatrix(proposedSentence, targetSentence);
		
		int w = 0;
		for (Word sw : proposedSentence) {
			int iw = 0;
			for (Word tw : targetSentence) {
				iwm.reversed = reversed;
				double mdist = iwm.similarity(sw, tw);
				dist.addDistance(reversed, w, iw, mdist, iwm.getName());
				++iw;
			}
			++w;
		}
		return dist;
	}
}
