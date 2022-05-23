package mt.core;

import java.io.PrintStream;

import mt.nlp.Sentence;

/// A direct implementation  of a sentenceAligment using the whole matrix
public class DistanceMatrix extends SimilarityMatrix implements SentenceAlignment {
	
	final int source_size;
	final int target_size;


	// for testing purposes
	public DistanceMatrix(double[][] m) {
		super(m);
		this.source_size = m.length;
		this.target_size = m[0].length;
	}

	public DistanceMatrix(int source_size, int target_size) {
		super(source_size,target_size);
		this.source_size = source_size;
		this.target_size = target_size;
	}

	public DistanceMatrix(Sentence source, Sentence target) {
		this(source.size(), target.size());
	}
	
	/// Default strategy is free alignment
	public boolean isAligned(int n1, int n2) {
		return getDistance(n1, n2) > 0;
	}

	public void dump(PrintStream s) {
		s.println("Source Length:"+source_size);
		s.println("Target Length:"+target_size);
	}

	public int bestMatchH(int c) {
		double max = -1;
		int maxp = -1;
		for (int i = 0; i < dist[c].length; ++i)
			if (dist[c][i] > max) {
				max = dist[c][i];
				maxp = i;
			}
		return maxp;
	}

	public int bestMatchV(int c) {
		double max = -1;
		int maxp = -1;
		for (int i = 0; i < dist.length; ++i)
			if (dist[i][c] > max) {
				max = dist[i][c];
				maxp = i;
			}
		return maxp;
	}

	public int getRowSize() {
		return  source_size;
	}

	public int getColumnSize() {
		return  target_size;
	}

	@Override
	public int[] getAlignment() {
		throw new RuntimeException("NOT IMPLEMENTED!");
	}

	@Override
	public void setAligned(int i, int j, String provenence) {
		throw new RuntimeException("NOT IMPLEMENTED!");
	}

	@Override
	public SentenceAlignment revert() {
		DistanceMatrix rev = new DistanceMatrix(target_size, source_size);
		for(int i=0;i<source_size;++i)
			for(int j=0;j<target_size;++j) {
				rev.setDistance(j, i, this.getDistance(i,j), this.getProvenance(i, j));
			}
		return rev;
	}

}
