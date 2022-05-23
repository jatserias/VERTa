package mt.core;

import java.io.PrintStream;

/**
 * 
 * An 1 to 1 alignment between the words of two sentences sentences
 * 
 * keep best alignment in both directions
 * 
 */
public interface SentenceAlignment {

	public boolean isAligned(int i, int j);

	int[] getAlignment();

	void setAligned(int i, int j, String provenence);

	public String getProvenance(int i, int j);

	public void dump(PrintStream err);

	public SentenceAlignment revert();
}
