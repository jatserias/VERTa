package mt;
/**
 * 
 * A class to filter out words from the lexical similarity
 * 
 * @author jordi
 *
 */
public class WordFilter {
/**
 * Filter out punctuation
 * @param w
 * @return
 */
	public static boolean  filter(final Word w) {
		char c = w.getFeature("POS").charAt(0);
		//if((c>'Z') || (c<'A')) System.err.println("FILTERING:"+w.getFeature("WORD"));
		return ((c>'Z') || (c<'A'));
	}
}
