package mt.core;

import java.io.PrintStream;

import mt.nlp.Sentence;
import mt.nlp.Word;

/// A direct implementation  of a sentenceAligment using the whole matrix
public class DistanceMatrix extends SimilarityMatrix implements SentenceAlignment {
	

	/// source Sentence
	private Sentence source;

	/// target Sentence
	private Sentence target;

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
		this.source = source;
		this.target = target;
	}
	
	/// Default strategy is free alignment
	public boolean isAligned(boolean reversed, int n1, int n2) {
		return getDistance(reversed, n1, n2) > 0;
	}

	public void dump(PrintStream s) {

		s.println("Distances S2T:");
		int wid = 0;
		for (Word w : source) {
			s.print(w.getFeature("WORD") + "_" + wid + ": ");
			int tid = 0;
			for (Word t : target) {
				s.print(" ");
				if (isAligned(false, wid, tid)) {
					s.print(t.getFeature("WORD") + "_" + tid + '=');
				}
				s.print(dist[wid][tid]);
				++tid;
			}
			s.println();
			++wid;
		}

		s.println("Distances T2S:");
		wid = 0;
		for (Word w : target) {
			s.print(w.getFeature("WORD") + "_" + wid + ": ");
			int tid = 0;
			for (Word t : source) {
				if (isAligned(true, wid, tid)) {
					s.print(" " + t.getFeature("WORD") + "_" + tid + "=");
				}
				++tid;
			}
			s.println();
			++wid;
		}
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

	public int getRowSize(boolean reversed) {
		return reversed ? target_size : source_size;
	}

	public int getColumnSize(boolean reversed) {
		return reversed ? source_size : target_size;
	}

	@Override
	public int[] getAlignment(boolean reversed) {
		throw new RuntimeException("NOT IMPLEMENTED!");
	}

	@Override
	public void setAligned(boolean reversed, int i, int j, String provenence) {
		throw new RuntimeException("NOT IMPLEMENTED!");
	}

	@Override
	public String provenance(boolean reversed, int i, int j) {
		return reversed ? prov[i][j].toString() : prov[j][i].toString();
	}

}
