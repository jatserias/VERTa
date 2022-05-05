package mt;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import mt.core.AlignmentBuilderBestMatch;
import mt.core.AlignmentImpl;
import mt.core.DistanceMatrix;
import mt.core.FeatureMetric;
import mt.core.MetricActivationCounter;
import mt.core.SentenceAlignment;
import mt.core.Similarity;
import mt.core.WeightedWordMetric;
import mt.core.WordFilter;
import mt.nlp.Sentence;
import mt.nlp.Word;
import verta.wn.WordNetAPI;
import verta.xml.AlignmentImplXMlDumper;
import verta.xml.WordMetricXMLDumper;

public class WordMetric {
	private static Logger LOGGER = Logger.getLogger(WordMetric.class.getName());

	MetricActivationCounter counters;
	private static final int MAX_FEATURE_WEIGHT = 100;

	static int NOWORDMETRICS = 2;

	public boolean FILTER_PUNCTUATION = false;

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
		 * there is an inconsistence between groupId and group number
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

	public double[] similarity(final Sentence proposedSentence, final Sentence referenceSentence, DistanceMatrix dist,
			PrintStream strace) {

		double[] res = new double[2];

		AlignmentImpl align = new AlignmentImpl(proposedSentence.size(), referenceSentence.size());

		double prec = sentenceSimilarity(dist, align, false, proposedSentence, referenceSentence);
		double rec = sentenceSimilarity(dist, align, true, referenceSentence, proposedSentence);
		res[0] = prec;
		res[1] = rec;

		// dump the alignment
		if (MTsimilarity.DUMP) {
			AlignmentImplXMlDumper.dump(align, strace);
		}

		return res;
	}

	public double sentenceSimilarity(DistanceMatrix dist, SentenceAlignment a, final boolean reversed,
			final Sentence proposedSentence, final Sentence targetSentence) {
		// calculate all distances
		int w = 0;
		for (Word sw : proposedSentence) {
			int iw = 0;
			for (Word tw : targetSentence) {
				this.reversed = reversed;
				double mdist = this.similarity(sw, tw);
				dist.setDistance(reversed, w, iw, mdist, "lex");
				++iw;
			}
			++w;
		}

		// TODO configure alignment strategy
		new AlignmentBuilderBestMatch().build(reversed, a, dist);

		// calculate scores given the selected alignment
		int nwords = 0;

		double res = 0;
		int i = 0;
		for (int i_al: a.getAlignment(reversed)) {
			if (!FILTER_PUNCTUATION || !WordFilter.filter(proposedSentence.get(i))) {
				if (i_al >= 0)
					res += dist.getDistance(reversed, i, i_al);
				nwords++;
			}
			i++;
		}

		return res / nwords++;
	}

	/**
	 * Calculates the similarity between two sentences
	 * 
	 * @param proposedSentence
	 * @param targetSentence
	 * @param wm               the word metric to be used
	 * @return the similarity [0-1]
	 */
	public double sentenceSimilarity(

			Integer align[], boolean[] taken, DistanceMatrix dist,

			final boolean reversed, final Sentence proposedSentence, final Sentence targetSentence) {

		double distBestWord[] = new double[proposedSentence.size()];
		int sw = 0;
		for (Word w : proposedSentence) {
			distBestWord[sw] = bestMatch(reversed, sw, w, targetSentence, align, taken, dist);
			++sw;
		}

		double sum = 0;

		for (int j = 0; j < distBestWord.length; ++j) {
			if (distBestWord[j] > 0)
				sum += distBestWord[j];
		}

		// @BUG return sum+100*sum/proposedSentence.size();
		return sum / proposedSentence.size();
	}

	/**
	 * returns the highest similarity according to Wordmetric wm between Word w (at
	 * position pos) with any the words in targetSentence
	 * 
	 * reversed was used to inverse relation in prec / recall like hypernym -
	 * hyponym
	 * 
	 */
	double bestMatch(final boolean reversed, final int pos, final Word w, final Sentence targetSentence,
			Integer[] align, boolean[] taken, DistanceMatrix dist) {
		boolean found = false;
		double max = 0;
		int maxword = -1;
		int iw = 0;
		while (iw < targetSentence.size() && !found) {
			// Not Aligned before
			if (!taken[iw]) {
				this.reversed = reversed;
				double mdist = this.similarity(w, targetSentence.get(iw));
				dist.setDistance(reversed, pos, iw, mdist, "lex");

				// TODO store the metric info to count best-similarity usage group+metric
				if (mdist > max) {
					max = mdist;
					maxword = iw;
				}
			}
			++iw;
		}

		// final result
		if (maxword > -1) {
			align[pos] = maxword;
			taken[maxword] = true;
		}
		return max;
	}

}
