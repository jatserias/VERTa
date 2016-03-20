package mt;

public class Ngram {
	protected static final Ngram[] EMPTY_NGRAM = new Ngram[0];

	public Ngram(int ngramStart, int ngramSize) {
		this.start=ngramStart;
		size=ngramSize;
	}
	int start;
	int size;
}

