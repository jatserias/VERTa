package mt.core;

import java.io.PrintStream;

public class AlignmentImplSingle implements ISentenceAlignment {

    private final int[] alignS2T;
    private final String[] provS2T;
    int colSize;

    public AlignmentImplSingle(int rowSize, int colSize) {
        this.colSize = colSize;
        alignS2T = new int[rowSize];
        provS2T = new String[rowSize];

        for (int i = 0; i < rowSize; ++i) {
            alignS2T[i] = -1;
        }

    }

    @Override
    public void setAligned(int i, int j, String provenance) {
        alignS2T[i] = j;
        provS2T[i] = provenance;
    }

    @Override
    public boolean isAligned(int i, int j) {
        return alignS2T[i] == j;
    }

    @Override
    public int[] getAlignment() {
        return alignS2T;
    }

    public String toString() {
        StringBuilder strace = new StringBuilder();
        for (int i : alignS2T) {
			strace.append("\t");
			strace.append(i);
		}
        strace.append("\n");
        return strace.toString();
    }

    @Override
    public String getProvenance(int i, int j) {
        return provS2T[i];
    }

    public ISentenceAlignment revert() {
        AlignmentImplSingle rev = new AlignmentImplSingle(colSize, alignS2T.length);
        for (int i = 0; i < alignS2T.length; ++i) {
            if (alignS2T[i] >= 0) rev.setAligned(alignS2T[i], i, provS2T[i]);
        }
        return rev;
    }

    @Override
    public void dump(PrintStream err) {
        err.println("row:" + alignS2T.length);
        err.println("col:" + colSize);

    }

}
