package mt.core;

import mt.nlp.Sentence;

import java.io.PrintStream;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
/// A direct implementation  of a sentenceAligment using the whole matrix
public class DistanceMatrix extends SimilarityMatrix implements ISentenceAlignment {

    private int rowSize;
    private int columnSize;


    // for testing purposes
    public DistanceMatrix(double[][] m) {
        super(m);
        this.rowSize = m.length;
        this.columnSize = m[0].length;
    }

    public DistanceMatrix(int source_size, int target_size) {
        super(source_size, target_size);
        this.rowSize = source_size;
        this.columnSize = target_size;
    }

    public DistanceMatrix(Sentence source, Sentence target) {
        this(source.size(), target.size());
    }

    /// Default strategy is free alignment
    public boolean isAligned(int n1, int n2) {
        return getDistance(n1, n2) > 0;
    }

    public void dump(PrintStream s) {
        s.println("Source Length:" + rowSize);
        s.println("Target Length:" + columnSize);
    }

    public int bestMatchH(int c) {
        double max = -1;
        int maxp = -1;
        for (int i = 0; i < getDist()[c].length; ++i)
            if (getDist()[c][i] > max) {
                max = getDist()[c][i];
                maxp = i;
            }
        return maxp;
    }

    public int bestMatchV(int c) {
        double max = -1;
        int maxp = -1;
        for (int i = 0; i < getDist().length; ++i)
            if (getDist()[i][c] > max) {
                max = getDist()[i][c];
                maxp = i;
            }
        return maxp;
    }

    @JsonIgnore
    @Override
    public int[] getAlignment() {
        throw new RuntimeException("NOT IMPLEMENTED!");
    }

    @JsonIgnore
    @Override
    public void setAligned(int i, int j, String provenence) {
        throw new RuntimeException("NOT IMPLEMENTED!");
    }

    @Override
    public ISentenceAlignment revert() {
        DistanceMatrix rev = new DistanceMatrix(columnSize, rowSize);
        for (int i = 0; i < rowSize; ++i)
            for (int j = 0; j < columnSize; ++j) {
                rev.setDistance(j, i, this.getDistance(i, j), this.getProvenance(i, j));
            }
        return rev;
    }

}
