package mt.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import verta.wn.WordNetAPI;

public class SentenceMetricBuilder {
	private static Logger LOGGER = Logger.getLogger(SentenceMetricBuilder.class.getName());

	static public SentenceMetric instantiateSentenceMetric(String className, String[] line,
			MetricActivationCounter counters, WordNetAPI wn) {
		SentenceMetric sm = null;
		try {
			@SuppressWarnings("rawtypes")
			Class[] partypes = new Class[1];
			partypes[0] = String.class;
			Class<?> cl = Class.forName(className);
			int narg = 5;
			Object[] arglist = new Object[line.length - narg + 1];
			@SuppressWarnings("rawtypes")
			Class[] clist = new Class[line.length - narg + 1];
			if (line.length - narg == 0) {
				clist[0] = counters.getClass();
				arglist[0] = counters;
			} else {
				LOGGER.info("sentenceMetric with " + (line.length - narg) + " args");
				clist[0] = counters.getClass();
				arglist[0] = counters;
				for (int i = narg + 1; i <= line.length; i++) {
					clist[i - narg] = String.class;
					arglist[i - narg] = line[i - 1];
				}
			}
			Constructor<?> ct = cl.getConstructor(clist);
			sm = (SentenceMetric) ct.newInstance(arglist);
			try {
				@SuppressWarnings("rawtypes")
				Class[] paramTypes = new Class[1];
				paramTypes[0] = WordNetAPI.class;
				Method method = sm.getClass().getMethod("Wn", paramTypes);
				LOGGER.info(className + " uses WN:" + wn);
				method.invoke(sm, new Object[] { wn });
				LOGGER.info("Metric setup!");
			} catch (Throwable throwable) {
				LOGGER.info("Something went wrong with " + className + ":" + throwable.getLocalizedMessage());
			}
		} catch (Exception e) {
			LOGGER.severe("Error trying to load SentenceMetric>" + className + "<");
			e.printStackTrace();
			System.exit(-1);
		}
		return sm;
	}
}
