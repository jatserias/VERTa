package mt.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import mt.WordMetric;
import mt.nlp.Sentence;
import verta.wn.WordNetAPI;
import verta.xml.VertaXMLDumper;

public class Verta {

	private static Logger LOGGER = Logger.getLogger(Verta.class.getName());
	
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
		for (WordMetric w : wms) {
			w.FILTER_PUNCTUATION = filter;
		}
	}
	
	/**
	 * 
	 * returns MetricResult P / R /F
	 * 
	 */
	public MetricResult similarity(final Sentence referenceSentence, final Sentence proposedSentence) {
		
		getTracer().start_lexical_metrics();
		
		/// Applies word-alignment metrics
		DistanceMatrix dist = new DistanceMatrix(proposedSentence, referenceSentence);

		/// store the partial results
		MetricResult tres = new MetricResult(dist);

		/**
		 * for w X w apply all word metrics
		 * 
		 * As a side result a matrix of distance (tres.dist) and an alignment between
		 * the sentences is build
		 */
		for (WordMetric iwm : wms) {
			// Return also dist matrix (internally uses align)
			double[] res = iwm.similarity(proposedSentence, referenceSentence, tres.dist, getTracer().strace);
			double prec = res[0];
			double rec = res[1];

			tres.add(iwm.getName(), iwm.getWeight(), prec, rec);

			getTracer().xml_dump_distances(referenceSentence, proposedSentence, iwm);

			/**
			 * @TODO ALERT we only align using the first lexical metric (should we add them
			 *       all?)
			 * 
			 *       this assignment is to avoid to update the distance matrix so only the
			 *       first lexical metric is used to calculate the alignment
			 */
			tres.dist = new DistanceMatrix(proposedSentence, referenceSentence);
		}

		getTracer().end_lexical_metrics();

		/**
		 * build Alignment based on the distance Matrix from the lexical components
		 */
		// We shoudl consider using different alignment builders
		// SentenceAlignment align = new AlignmentBuilderFirstLeft2Rigth().build(dist);
		// SentenceAlignment align = new AlignmentBuilderBestMatch().build(dist);
		SentenceAlignment align = dist;

		/***
		 * Call sentence level metrics using align[] for word metrics
		 */

		getTracer().star_sentence_metrics();

		for (WeightedSentenceMetric iwm : sm) {
			SimilarityResult res = iwm.similarity(proposedSentence, referenceSentence, align, getTracer().strace);
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

}
