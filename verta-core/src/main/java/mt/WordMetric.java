package mt;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import lombok.Getter;
import lombok.Setter;
import mt.core.FeatureMetric;
import mt.core.MetricActivationCounter;
import mt.core.IFeaturesWordSimilarity;
import mt.core.WeightedWordMetric;
import mt.nlp.Word;
import verta.wn.IWordNet;
import verta.xml.WordMetricXMLDumper;


@Getter
@Setter
public class WordMetric {
	@JsonIgnore
	MetricActivationCounter counters;
	static final int MAX_FEATURE_WEIGHT = 100;

	// we should group metrics by id
	public HashMap<String, WeightedWordMetric> featureMetrics;
	/// group weigth
	private double weight;
	/// name
	private String name;

	public WordMetric() {
		featureMetrics = new HashMap<>();
	}

	public WordMetric(String name, BufferedReader config, double groupWeight, String configFilename,
			MetricActivationCounter counters, IWordNet wn) {
		this.name = name;
		this.counters = counters;
		featureMetrics = new HashMap<>();
		WordMetricLoader.load(this, config, groupWeight, configFilename, wn);
	}

	/**
     * word similarity is just the sum of feature similarity (@TODO normalization
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
			while (contrib <= IFeaturesWordSimilarity.MIN_VAL && f < x.size()) {
				FeatureMetric fm = x.get(f);
				contrib = fm.similarity(proposedWord, targetWord);
				active = contrib > IFeaturesWordSimilarity.MIN_VAL;

				if (counters != null)
					counters.increase(fm.toString() + Arrays.asList(fm.getFeatureNames()), 1);

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

	public static void main(String args[]) {
		YAMLFactory f = new YAMLFactory();
		f.disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID);
		final ObjectMapper mapper = new ObjectMapper(f);
		IWordNet wordnetI = null;
		MetricActivationCounter counters = new MetricActivationCounter();
		WordMetric serializedEx = new WordMetric("word metric",
				new BufferedReader(new StringReader("1\tWORD\t100\tmt.SimilarityEqual\n"))
		, 1.0, "config filename", counters,  wordnetI);
		String jsonDataString;
		
	 try {
		 jsonDataString = mapper.writeValueAsString(serializedEx);
		 System.err.println(jsonDataString);
		 WordMetric reread = mapper.readValue(jsonDataString, WordMetric.class);
		 System.err.println(reread.toString());
	 } catch (JsonProcessingException e) {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
	 }
	 }
}

