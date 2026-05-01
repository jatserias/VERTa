package mt.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TriplesMatcherLoader {

    public static TriplesMatcher load(String filename, BufferedReader config) throws IOException {
        TriplesMatcher builder = new TriplesMatcher();
        String buff = null;
        try {

            // read weights
            while ((buff = config.readLine()) != null && buff.trim().startsWith(TriplesMatcher.LABEL_PAIR_SEPARATOR))
                ;

            if (buff == null) {
                throw new RuntimeException(
                        "Format ERROR, empty/non existing file on the triple config file >" + filename + "<");
            }

            String[] wbuff = buff.split("\t");
            int i = 0;
            TriplesMatcher.COMPLETE_WEIGHT.setScore(Double.parseDouble(wbuff[i++]));
            TriplesMatcher.PARTIAL_NO_MOD_WEIGHT.setScore(Double.parseDouble(wbuff[i++]));
            TriplesMatcher.PARTIAL_NO_HEAD_WEIGHT.setScore(Double.parseDouble(wbuff[i++]));
            TriplesMatcher.PARTIAL_NO_LABEL_WEIGHT.setScore(Double.parseDouble(wbuff[i]));

            HashMap<String,Double> labels = new HashMap<>();
            /// Read rules
            while ((buff = config.readLine()) != null) {
                if (!buff.trim().startsWith(TriplesMatcher.LABEL_PAIR_SEPARATOR)) {
                    String[] label = buff.split("\t");
                    labels.put(label[0] + TriplesMatcher.LABEL_PAIR_SEPARATOR + label[1], Double.parseDouble(label[2]));
                }
            }
            builder.setLabelMatch(labels);
        } catch (Exception e) {
            log.error("Error reading triplet config file:" + buff, e);
            throw e;
        }
        return builder;
    }
}
