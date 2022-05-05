package mt.core;

/// This keeps an alignment in both directions
public class AlignmentImpl implements SentenceAlignment {

	private int[] alignS2T;
	private int[] alignT2S;
	private Object[] provS2T;
	private Object[] provT2S;

	public AlignmentImpl(int rowsize, int colsize) {
		alignS2T = new int[rowsize];
		alignT2S = new int[colsize];
		provS2T = new Object[rowsize];
		provT2S = new Object[colsize];
		;
		for (int i = 0; i < rowsize; ++i) {
			alignS2T[i] = -1;
		}
		for (int i = 0; i < colsize; ++i) {
			alignT2S[i] = -1;
		}
	}

	@Override
	public void setAligned(boolean reversed, int i, int j, String provenence) {
		if (reversed) {
			alignT2S[i] = j;
			provT2S[i] = provenence;
		} else {
			alignS2T[i] = j;
			provS2T[i] = provenence;
		}
	}

	@Override
	public boolean isAligned(boolean reversed, int i, int j) {
		return reversed ? alignT2S[j] == i : alignS2T[j] == i;
	}

	@Override
	public int[] getAlignment(boolean reversed) {
		return reversed ? alignT2S : alignS2T;
	}

	public String toString() {
		StringBuffer strace = new StringBuffer();
		for (int i : alignS2T)
			strace.append("\t" + i);
		strace.append("\n");

		for (int i : alignT2S)
			strace.append("\t" + i);
		strace.append("\n");
		return strace.toString();
	}

	@Override
	public String provenance(boolean reversed, int i, int j) {
		return reversed ? provT2S[i].toString() : provS2T[i].toString();
	}
}
