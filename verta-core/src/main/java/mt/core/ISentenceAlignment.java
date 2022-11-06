package mt.core;

import java.io.PrintStream;

/**
 * An 1 to 1 alignment between the words of two sentences sentences
 * keep the best alignment in both directions
 **/
public interface ISentenceAlignment {

    boolean isAligned(int i, int j);

    int[] getAlignment();

    void setAligned(int i, int j, String provenance);

    String getProvenance(int i, int j);

    void dump(PrintStream err);

    ISentenceAlignment revert();
}
