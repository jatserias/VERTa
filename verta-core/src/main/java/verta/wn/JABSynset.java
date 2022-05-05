package verta.wn;

public interface JABSynset {

	JABSynset[] getHypernyms();

	String[] getWordForms();

	/**
	 * 
	 * @param wf word form
	 * @return Returns the tag count for the sense entry. A tag count is a non-negative integer that represents the number of times the sense is tagged in various semantic concordance texts.
	 */
	int getTagCount(String wf);
	
	public boolean equals(Object aThat);
}
