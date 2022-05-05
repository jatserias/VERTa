package mt.nlp;

public class Ngram {
	public static final Ngram[] EMPTY_NGRAM = new Ngram[0];

	public Ngram(int ngramStart, int ngramSize) {
		this.setStart(ngramStart);
		setSize(ngramSize);
	}

	private int start;
	private int size;
	
	@Override
    public boolean equals(Object obj)
    {
          
		if(this == obj) return true;
          
        if(obj == null || obj.getClass()!= this.getClass())
            return false;
          
        Ngram ngram = (Ngram) obj;
          
        return (ngram.getStart() == this.getStart() && ngram.getSize() == this.getSize());
    }
	
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	public String toString() {
		return String.format("start: %d size: %d", getStart(), getSize());
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}
}
