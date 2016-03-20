package mt;

public interface JABSynset {

	JABSynset[] getHypernyms();

	String[] getWordForms();

	int getTagCount(String wf);
	
	public boolean equals(Object aThat);
}
