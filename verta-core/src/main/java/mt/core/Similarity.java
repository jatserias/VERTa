package mt.core;

import mt.nlp.Word;

/**
 * interface to an asymmetric similarity function
 * <p>
 * CONTRACT:
 * <p>
 * return [0..1] sim(x,x)=1
 */
public interface Similarity {
    /// max value for a similarity function
    double MAX_VAL = 1.0;
    /// min value for a similarity function
    double MIN_VAL = 0.0;

    /// calculate the similarity between proosedWord and referenceWord base on
    /// feature (featureName)
    double similarity(String[] featureNames, Word proposedWord, Word referenceWord);

    String getClassName();

    void setReversed(boolean reversed);

    double getWeight();

    void setWeight(double w);
}
