package mt.core;

import verta.wn.WordNetAPI;

public abstract class WnBaseSimilarity extends BaseSimilarity implements Similarity {
	public static WordNetAPI wn;

	public WnBaseSimilarity Wn(WordNetAPI wn) {
		WnBaseSimilarity.wn = wn;
		return this;
	}
}
