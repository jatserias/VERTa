package mt;

import java.io.BufferedReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import lombok.extern.slf4j.Slf4j;
import mt.core.FeatureMetric;
import mt.core.IFeaturesWordSimilarity;
import mt.core.WeightedWordMetric;
import verta.wn.IWordNet;

@Slf4j
public class WordMetricLoader {

    public static IFeaturesWordSimilarity instantiateSimilarity(String className, double weight, String[] line, int nPar,
    		IWordNet wn) throws Exception {
    	IFeaturesWordSimilarity sm = null;
    	try {
    		Class<?>[] parTypes = new Class[1];
    		parTypes[0] = java.lang.String.class;
    		Class<?> cl = Class.forName(className);
    
    		int nArgs = 3 + nPar;
    		Object[] argList = new Object[line.length - nArgs];
    		if (line.length - nArgs >= 0) System.arraycopy(line, nArgs, argList, 0, line.length - nArgs);

			System.err.println("-----------");
			// @TODO check which constructor to call
			Constructor<?> ct = cl.getConstructors()[0];
    		for(Constructor<?>  constructor : cl.getConstructors()) {
				System.err.println(constructor.getName());
				System.err.println(constructor.getParameterTypes());
				System.err.println(constructor.getParameterTypes().length);
				System.err.println(line.length - nArgs);
				if(constructor.getParameterTypes().length ==  (line.length - nArgs)) {
					ct = constructor;
					System.err.println("Choosing: "+constructor.getParameterTypes().length);
				}
			}
			System.err.println("-----------");
    		sm = (IFeaturesWordSimilarity) ct.newInstance(argList);
			System.err.println("-----------");
    		try {
    			@SuppressWarnings("rawtypes")
    			Class[] paramTypes = new Class[1];
    			paramTypes[0] = IWordNet.class;
    			Method method = sm.getClass().getMethod("Wn", paramTypes);
    			WordMetricLoader.log.info(className + " uses WN:" + wn);
    			method.invoke(sm, new Object[] { wn });
    			WordMetricLoader.log.info("Metric setup!");
    		} catch (java.lang.NoSuchMethodException v) {
    			// No wn set up method
    		}
    
    		sm.setWeight(weight);
    
    	} catch (Exception e) {
    		WordMetricLoader.log.error("Error trying to load Similarity Class >" + className + "<");
    		throw e;
    	}
    	return sm;
    }

	public static void load(WordMetric wm, BufferedReader config, double groupWeight, String filename, IWordNet wn) {
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
					if (weight > WordMetric.MAX_FEATURE_WEIGHT)
						log.warn("Warning Weight>>" +  WordMetric.MAX_FEATURE_WEIGHT + " in metric config file at " + buff);

					IFeaturesWordSimilarity sm = WordMetricLoader.instantiateSimilarity(className, weight, line, nPar, wn);

					// Add a feature metric into the grup
					WeightedWordMetric group = wm.featureMetrics.get(groupId);
					// ERROR we should relate ngroup to grouID (it may be inconsistent)
					if (group == null)
						group = new WeightedWordMetric(1.0); //TODO CHECK what we need weight (groupWeight);
					group.add(new FeatureMetric(featureName, sm, weight));
					wm.featureMetrics.put(groupId, group);
				}

				wm.setWeight(groupWeight);
			}

		} catch (Exception e) {
			log.error("Format Error in Metric configuration file", e);
			System.exit(-1);
		}
	}
}
