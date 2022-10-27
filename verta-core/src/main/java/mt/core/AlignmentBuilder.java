package mt.core;

/// Given a distance Matrix between two sentences build the best alignment
public interface AlignmentBuilder {

    void build(SentenceAlignment res, final DistanceMatrix d);

}
