package mt.core;

import verta.wn.IWordNet;

public abstract class WnBaseSimilarity extends BaseSimilarity implements Similarity {
	public static IWordNet wn;

	public WnBaseSimilarity Wn(IWordNet wn) {
		WnBaseSimilarity.wn = wn;
		return this;
	}
}
