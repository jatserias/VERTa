package mt.core;

import java.io.PrintStream;

public class AlignmentImplSingle implements SentenceAlignment {

	private int[] alignS2T;
	private String[] provS2T;
	int colsize;

	public AlignmentImplSingle(int rowsize, int colsize) {
		this.colsize = colsize;
		alignS2T = new int[rowsize];
		provS2T = new String[rowsize];

		for (int i = 0; i < rowsize; ++i) {
			alignS2T[i] = -1;
		}

	}

	@Override
	public void setAligned(int i, int j, String provenence) {
		alignS2T[i] = j;
		provS2T[i] = provenence;
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
		StringBuffer strace = new StringBuffer();
		for (int i : alignS2T)
			strace.append("\t" + i);
		strace.append("\n");
		return strace.toString();
	}

	@Override
	public String getProvenance(int i, int j) {
		return provS2T[i].toString();
	}

	public SentenceAlignment revert() {
		AlignmentImplSingle rev = new AlignmentImplSingle(colsize, alignS2T.length);
		for (int i = 0; i < alignS2T.length; ++i) {
			if (alignS2T[i] >= 0)
				rev.setAligned(alignS2T[i], i, provS2T[i]);
		}
		return rev;
	}

	@Override
	public void dump(PrintStream err) {
		err.println("row:" + alignS2T.length);
		err.println("col:" + colsize);

	}

}
