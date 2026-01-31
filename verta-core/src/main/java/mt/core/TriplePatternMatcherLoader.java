package mt.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class TriplePatternMatcherLoader extends TriplesMatcherLoader {

    public static TriplePatternMatcher load(String filename, BufferedReader config) throws IOException {
		TriplePatternMatcher builder = new TriplePatternMatcher();
        readSets(builder, config);
        readPatterns(builder, config);
		return builder;
    }


    private static void readSets(TriplePatternMatcher builder, BufferedReader fconf) throws IOException {
        String buff;
        while ((buff = fconf.readLine()) != null && buff.startsWith("##%SETS")) ;
		HashMap<String, LabelSet>groups = new HashMap<>();
        while ((buff = fconf.readLine()) != null && !buff.startsWith("%%#PATTERNS")) {
            if (!buff.startsWith(TriplePatternMatcher.COMMENT)) {
                LabelSet s = new LabelSet(buff);
                groups.put(s.getId(), s);
            }
        }
		builder.setGroups(groups);
    }

    private static void readPatterns(TriplePatternMatcher builder, BufferedReader fconf) throws IOException {
        String buff;
        while ((buff = fconf.readLine()) != null) {
            if (!buff.startsWith(TriplePatternMatcher.COMMENT)) {
                if (buff.trim().length() > 0) {
                    TripleMatchPattern s = TripleMatchPatternLoader.readPattern(buff, builder.getGroups());
                    builder.getLp().add(s);
                }
            }
        }
    }
}