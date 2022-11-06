package mt;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import mt.core.FeatureMetric;
import mt.core.MetricActivationCounter;
import mt.core.Similarity;
import mt.core.WeightedWordMetric;
import mt.nlp.Word;
import verta.wn.IWordNet;
import verta.xml.WordMetricXMLDumper;

@Slf4j
public class WordMetric {
	MetricActivationCounter counters;
	private static final int MAX_FEATURE_WEIGHT = 100;

	// we should group metrics by id
	public HashMap<String, WeightedWordMetric> featureMetrics;

	//public boolean reversed;

	public double groupWeight;

	/// name
	String name;

	public static Similarity instantiateSimilarity(String className, double weight, String[] line, int nPar,
			IWordNet wn) {
		Similarity sm = null;
		try {
			Class<?>[] parTypes = new Class[1];
			parTypes[0] = java.lang.String.class;
			Class<?> cl = Class.forName(className);

			// @TODO check which constructor to call
			Constructor<?> ct = cl.getConstructors()[0];

			int nArgs = 3 + nPar;
			Object[] argList = new Object[line.length - nArgs];
			if (line.length - nArgs >= 0) System.arraycopy(line, nArgs, argList, 0, line.length - nArgs);
			sm = (Similarity) ct.newInstance(argList);

			try {
				@SuppressWarnings("rawtypes")
				Class[] paramTypes = new Class[1];
				paramTypes[0] = IWordNet.class;
				Method method = sm.getClass().getMethod("Wn", paramTypes);
				log.info(className + " uses WN:" + wn);
				method.invoke(sm, new Object[] { wn });
				log.info("Metric setup!");
			} catch (java.lang.NoSuchMethodException v) {
				// No wn set up method
			}

			sm.setWeight(weight);

		} catch (Exception e) {
			log.error("Error trying to load Similarity Class >" + className + "<");
			System.exit(-1);

		}
		return sm;
	}

	public WordMetric() {
		featureMetrics = new HashMap<>();
	}

	public WordMetric(String name, BufferedReader config, double groupWeight, String configFilename,
			MetricActivationCounter counters, IWordNet wn) {
		this.name = name;
		this.counters = counters;
		featureMetrics = new HashMap<>();
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
	 * How a set of metrics is applied to a pair of words*
	 * trace
	 * &lt; ft type ="TYPE_PARAM"/&gt; &lt;group id="NGROUP"&gt;
	 * &lt;mt feat="FEATURE_NAME" sim="JAVACLASS" simid="ID" active="COLOR" pword=
	 * "PROOSEDWORD" rword="TARGETWORD" weight="DIST"/&gt; ... &lt;/group&gt; &lt;/ft&gt;
	 * 
	 */
	public double similarity(final Word proposedWord, final Word targetWord, PrintStream pout, String type) {
		double sum = 0;

		//TODO FIX WEIGHT
		WordMetricXMLDumper.xml_wm_start_ft(pout, type);

		/*
		 * there is an inconsistency between groupId and group number
		 */
		// for every group metric
		int nGroup = 0;
		for (String group : featureMetrics.keySet()) {
			WordMetricXMLDumper.xml_wm_start_group(pout, nGroup);
			WeightedWordMetric x = featureMetrics.get(group);
			double contrib = 0.0;
			int f = 0;
			// initilized to true
			boolean active;
			while (contrib <= Similarity.MIN_VAL && f < x.size()) {
				FeatureMetric fm = x.get(f);
				contrib = fm.similarity(proposedWord, targetWord);
				active = contrib > Similarity.MIN_VAL;

				if (counters != null)
					counters.increase(fm.getClassName() + Arrays.asList(fm.featureNames), 1);

				// trace
				WordMetricXMLDumper.xml_wm_dump(proposedWord, targetWord, pout, contrib, f, active, fm);
				f++;
			}

			sum = sum + x.getWeight() * contrib;
			nGroup++;
			WordMetricXMLDumper.xml_wm_end_group(pout);
		}
		WordMetricXMLDumper.xml_wm_end_ft(pout);
		return sum / MAX_FEATURE_WEIGHT;
	}

	public void load(BufferedReader config, double groupWeight, String filename, IWordNet wn) {
		try {
			String buff;
			while ((buff = config.readLine()) != null && !buff.trim().startsWith("FGROUP")) {

				log.info("proc:" + buff);

				// comments start with #
				if (!buff.trim().startsWith("#")) {
					String[] line = buff.split("[ \t]+");
					if (line.length < 4) {
						log.error("Format ERROR on the metric config file >" + filename + "< AT LINE:" + buff);
						System.exit(-1);
					}
					// Similarity sm = null;//TODO Load Class by name line[1];
					int nPar = 1;
					String groupId = line[0];
					String className = line[nPar + 2];
					String featureName = line[nPar];
					double weight = Double.parseDouble(line[nPar + 1]);
					if (weight > MAX_FEATURE_WEIGHT)
						log.warn("Warning Weight>>" + MAX_FEATURE_WEIGHT + " in metric config file at " + buff);

					Similarity sm = instantiateSimilarity(className, weight, line, nPar, wn);

					// Add a feature metric into the grup
					WeightedWordMetric group = featureMetrics.get(groupId);
					// ERROR we should relate ngroup to grouID (it may be inconsistent)
					if (group == null)
						group = new WeightedWordMetric(1.0); //TODO CHECK what we need weight (groupWeight);
					group.add(new FeatureMetric(featureName, sm, weight));
					featureMetrics.put(groupId, group);
				}

				this.groupWeight = groupWeight;
			}

		} catch (Exception e) {
			log.error("Format Error in Metric configuration file", e);
			System.exit(-1);
		}
	}

	public double getWeight() {
		return groupWeight;
	}

	public String getName() {
		return name;
	}

}
