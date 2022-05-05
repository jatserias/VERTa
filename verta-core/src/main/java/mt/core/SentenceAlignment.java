package mt.core;

/**
 * 
 * An 1 to 1 alignment between the words of two sentences sentences
 * 
 * keep best aligment in both directions
 * 
 */
public interface SentenceAlignment {

	public boolean isAligned(boolean reversed, int i, int j);

	int[] getAlignment(boolean reversed);

	void setAligned(boolean reversed, int i, int j, String provenence);

	public String provenance(boolean reversed, int i, int j);
}
