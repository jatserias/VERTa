package mt;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import mt.core.DistanceMatrix;
import mt.core.FeatureMetric;
import mt.core.MetricActivationCounter;
import mt.core.Similarity;
import mt.core.WeightedWordMetric;
import mt.nlp.Sentence;
import mt.nlp.Word;
import verta.wn.WordNetAPI;
import verta.xml.WordMetricXMLDumper;

public class WordMetric {
	private static Logger LOGGER = Logger.getLogger(WordMetric.class.getName());

	MetricActivationCounter counters;
	private static final int MAX_FEATURE_WEIGHT = 100;

	static int NOWORDMETRICS = 2;

	// we should group metrics by id
	public HashMap<String, WeightedWordMetric> featureMetrics;

	public boolean reversed;

	public double groupWeight;

	/// name
	String name;

	public static Similarity instantiateSimilarity(String className, double weight, String[] line, int npar,
			WordNetAPI wn) {
		Similarity sm = null;
		try {
			Class<?> partypes[] = new Class[1];
			partypes[0] = java.lang.String.class;
			Class<?> cl = Class.forName(className);

			// @TODO check which constructor to call
			Constructor<?> ct = cl.getConstructors()[0];

			int narg = 3 + npar;
			Object arglist[] = new Object[line.length - narg];
			for (int i = narg; i < line.length; ++i)
				arglist[i - narg] = line[i];
			sm = (Similarity) ct.newInstance(arglist);

			try {
				@SuppressWarnings("rawtypes")
				Class[] paramTypes = new Class[1];
				paramTypes[0] = WordNetAPI.class;
				Method method = sm.getClass().getMethod("Wn", paramTypes);
				LOGGER.info(className + " uses WN:" + wn);
				method.invoke(sm, new Object[] { wn });
				LOGGER.info("Metric setup!");
			} catch (java.lang.NoSuchMethodException v) {
				// No wn set up method
			}

			sm.setWeight(weight);

		} catch (Exception e) {
			LOGGER.severe("Error trying to load Similarity Class >" + className + "<");
			System.exit(-1);

		}
		return sm;
	}

	public WordMetric() {
		featureMetrics = new HashMap<String, WeightedWordMetric>();
	}

	public WordMetric(String name, BufferedReader config, double groupWeight, String configFilename,
			MetricActivationCounter counters, WordNetAPI wn) {
		this.name = name;
		this.counters = counters;
		featureMetrics = new HashMap<String, WeightedWordMetric>();
		load(config, groupWeight, configFilename, wn);
	}

	/**
	 * 
	 * 
	 * /* word similarity is just the sum of feature similarity (@TODO normalization
	 * on the number of features maybe needed)
	 */
	public double similarity(Word proposedWord, Word targetWord) {
		return similarity(proposedWord, targetWord, null, "");
	}

	/**
	 * 
	 * How a set of metrics is applied to a pair of words
	 * 
	 * trace
	 * 
	 * <ft type ="TYPE_PARAM"/> <group id="NGROUP">
	 * <mt feat="FEATURE_NAME" sim="JAVACLASS" simid="ID" active="COLOR" pword=
	 * "PROOSEDWORD" rword="TARGETWORD" weight="DIST"/> ... </group> </ft>
	 * 
	 */
	public double similarity(final Word proposedWord, final Word targetWord, PrintStream pout, String type) {
		double sum = 0;

		// @TODO FIX WEIGHT
		WordMetricXMLDumper.xml_wm_start_ft(pout, type);

		/**
		 * there is an inconsistency between groupId and group number
		 */
		// for every group metric
		int ngroup = 0;
		for (String group : featureMetrics.keySet()) {
			WordMetricXMLDumper.xml_wm_start_group(pout, ngroup);
			WeightedWordMetric x = featureMetrics.get(group);
			double contrib = 0.0;
			int f = 0;
			boolean active = true;
			while (contrib <= Similarity.MINVAL && f < x.size()) {
				FeatureMetric fm = x.get(f);
				fm.reversed = reversed;
				contrib = fm.similarity(proposedWord, targetWord);
				active = contrib > Similarity.MINVAL;

				if (counters != null)
					counters.increase(fm.getClassName() + Arrays.asList(fm.featureNames), contrib, reversed);

				// trace
				WordMetricXMLDumper.xml_wm_dump(proposedWord, targetWord, pout, contrib, f, active, fm);
				f++;
			}

			sum = sum + x.getWeight() * contrib;
			ngroup++;
			WordMetricXMLDumper.xml_wm_end_group(pout);
		}
		WordMetricXMLDumper.xml_wm_end_ft(pout);
		return sum / MAX_FEATURE_WEIGHT;
	}

	public void load(BufferedReader config, double groupWeight, String filename, WordNetAPI wn) {
		try {
			String buff;
			while ((buff = config.readLine()) != null && !buff.trim().startsWith("FGROUP")) {

				LOGGER.info("proc:" + buff);

				// comments start with #
				if (!buff.trim().startsWith("#")) {
					String line[] = buff.split("[ \t]+");
					if (line.length < 4) {
						LOGGER.severe("Format ERROR on the metric config file >" + filename + "<");
						LOGGER.severe("AT LINE:" + buff);
						System.exit(-1);
					}
					// Similarity sm = null;//@TODO Load Class by name line[1];
					int npar = 1;
					String grupId = line[0];
					String className = line[npar + 2];
					String featureName = line[npar];
					double weight = Double.parseDouble(line[npar + 1]);
					if (weight > MAX_FEATURE_WEIGHT)
						System.err
								.println("Warning Weight>>" + MAX_FEATURE_WEIGHT + " in metric config file at " + line);// weight=100;

					Similarity sm = instantiateSimilarity(className, weight, line, npar, wn);

					// Add a feature metric into the grup
					WeightedWordMetric group = featureMetrics.get(grupId);
					// ERROR we should relate ngroup to grouID (it may be inconsistent)
					if (group == null)
						group = new WeightedWordMetric(1.0); // @TODO CHECK what we need weight (groupWeight);
					group.add(new FeatureMetric(featureName, sm, weight));
					featureMetrics.put(grupId, group);
				}

				this.groupWeight = groupWeight;
			}

		} catch (Exception e) {
			LOGGER.severe("Format Error in Metric configuration file");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public double getWeight() {
		return groupWeight;
	}

	public String getName() {
		return name;
	}

	/**
	 * 
	 * 
	 * Sentence level
	 * 
	 * 
	 * 
	 */


	/**
	 * Calculates the similarity between two sentences
	 * 
	 * @param proposedSentence
	 * @param targetSentence
	 * @param wm               the word metric to be used
	 * @return the similarity [0-1]
	 */
	/*
	 * @Deprecated double sentenceSimilarity(
	 * 
	 * Integer align[], boolean[] taken, DistanceMatrix dist,
	 * 
	 * final boolean reversed, final Sentence proposedSentence, final Sentence
	 * targetSentence) {
	 * 
	 * calculate_word_similarity_matrix(dist, reversed, proposedSentence,
	 * targetSentence);
	 * 
	 * int proposed_size = proposedSentence.size(); int target_size =
	 * targetSentence.size();
	 * 
	 * return calculate_sentence_aggregated_word_similarity(align, taken, dist,
	 * reversed, proposed_size, target_size); }
	 * 
	 * private double calculate_sentence_aggregated_word_similarity(Integer[] align,
	 * boolean[] taken, DistanceMatrix dist, final boolean reversed, int
	 * proposed_size, int target_size) { double distBestWord[] = new
	 * double[proposed_size];
	 * 
	 * for (int sw = 0; sw<proposed_size; ++sw) { double max = 0; int maxword = -1;
	 * int iw = 0; while (iw < target_size) { // Not Aligned before double mdist =
	 * dist.getDistance(reversed, sw, iw); if (!taken[iw]) { // TODO store the
	 * metric info to count best-similarity usage group+metric if (mdist > max) {
	 * max = mdist; maxword = iw; } } ++iw; }
	 * 
	 * // final result if (maxword > -1) { align[sw] = maxword; taken[maxword] =
	 * true; } distBestWord[sw] = max; ++sw; }
	 * 
	 * double sum = 0;
	 * 
	 * for (int j = 0; j < distBestWord.length; ++j) { if (distBestWord[j] > 0) sum
	 * += distBestWord[j]; }
	 * 
	 * // @BUG return sum+100*sum/proposedSentence.size(); return sum /
	 * proposed_size; }
	 * 
	 * private void calculate_word_similarity_matrix(DistanceMatrix dist, final
	 * boolean reversed, final Sentence proposedSentence, final Sentence
	 * targetSentence) { this.reversed = reversed; for(int i_sw=0; i_sw <
	 * proposedSentence.size(); ++i_sw) { for(int i_tw=0; i_tw <
	 * targetSentence.size(); ++i_tw) { double mdist =
	 * this.similarity(proposedSentence.get(i_sw), targetSentence.get(i_tw));
	 * dist.setDistance(reversed, i_sw, i_tw, mdist, this.getName()); } } }
	 */

}
