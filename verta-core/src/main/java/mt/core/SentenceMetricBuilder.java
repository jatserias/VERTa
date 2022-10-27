package mt.core;

import lombok.extern.slf4j.Slf4j;
import verta.wn.WordNetAPI;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@Slf4j
public class SentenceMetricBuilder {

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
                log.info("sentenceMetric with " + (line.length - narg) + " args");
                clist[0] = counters.getClass();
                arglist[0] = counters;
                for (int i = narg + 1; i <= line.length; i++) {
                    clist[i - narg] = String.class;
                    arglist[i - narg] = line[i - 1];
                }
            }
            Constructor<?> ct = cl.getConstructor(clist);
            sm = (SentenceMetric) ct.newInstance(arglist);
            // try setting up Wordnet (may fail if not needed
            try {
                @SuppressWarnings("rawtypes")
                Class[] paramTypes = new Class[1];
                paramTypes[0] = WordNetAPI.class;
                Method method = sm.getClass().getMethod("Wn", paramTypes);
                log.info(className + " uses WN:" + wn);
                method.invoke(sm, wn);
                log.info("Wn setup for metric " + className);
            } catch (Throwable throwable) {
                log.warn("The " + className + " may not need wordnet setup:" + throwable.getLocalizedMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error trying to load SentenceMetric>" + className + "<", e);
        }
        return sm;
    }
}
