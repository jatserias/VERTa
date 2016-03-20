package mt;

import java.io.PrintStream;
/**
 * 
 * An 1 to 1 alignment between the words of two sentences sentences
 * 
 * keep best aligment in both directions
 * 
 * @author jordi
 *
 */
public interface SentenceAlignment {

	public boolean isAligned(boolean reversed, int i, int j);

	int[] aligned(boolean reversed);

	void setAligned(boolean reversed, int i, int j, String provenence);

	public void dump(PrintStream err);

	public String provenence(boolean reversed, int i, int j);
}
