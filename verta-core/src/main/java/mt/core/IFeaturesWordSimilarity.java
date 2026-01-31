package mt.core;

import mt.SimilarityEqual;
import mt.SimilarityHypernymWn;
import mt.SimilarityHypernymWnMFS;
import mt.SimilarityHypernymWnPos;
import mt.SimilarityHypernymWnPosMFS;
import mt.SimilarityHyponymWn;
import mt.SimilarityHyponymWnMFS;
import mt.SimilarityHyponymWnPos;
import mt.SimilarityHyponymWnPosMFS;
import mt.SimilarityLemma;
import mt.SimilaritySynonymDicc;
import mt.SimilaritySynonymWn;
import mt.SimilaritySynonymWnPos;
import mt.nlp.Word;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * interface to an asymmetric similarity function
 * <p>
 * CONTRACT:
 * <p>
 * return [0..1] sim(x,x)=1
 */

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME, 
  include = JsonTypeInfo.As.PROPERTY, 
  property = "type")
@JsonSubTypes({ 
  @Type(value = SimilarityEqual.class, name = "SimilarityEqual"), 
  @Type(value = SimilarityHypernymWn.class, name = "SimilarityHypernymWn"),
  @Type(value = SimilarityHypernymWnMFS.class, name ="SimilarityHypernymWnMFS"),
  @Type(value = SimilarityHypernymWnPos.class, name ="SimilarityHypernymWnPos"),
  @Type(value = SimilarityHypernymWnPosMFS.class, name ="SimilarityHypernymWnPosMFS"),
  @Type(value = SimilarityHyponymWn.class, name = "SimilarityHyponymWn"), 
  @Type(value = SimilarityHyponymWnMFS.class, name ="SimilarityHyponymWnMFS"),
  @Type(value = SimilarityHyponymWnPos.class, name = "SimilarityHyponymWnPos"),
  @Type(value = SimilarityHyponymWnPosMFS.class, name = "SimilarityHyponymWnPosMFS"),
  @Type(value = SimilarityLemma.class, name = "SimilarityLemma"),
  @Type(value = SimilaritySynonymWn.class, name = "SimilaritySynonymWn"),
  @Type(value = SimilaritySynonymWnPos.class, name = "SimilaritySynonymWnPos"),
  @Type(value = SimilaritySynonymDicc.class, name = "SimilaritySynonymDicc"),
  @Type(value = SimilarityHyponymWnMFS.class, name= "SimilarityHyponymWnMFS")
})
public interface IFeaturesWordSimilarity {
    /// max value for a similarity function
    final static double MAX_VAL = 1.0;
    /// min value for a similarity function
    final static double MIN_VAL = 0.0;

    /// calculate the similarity between proosedWord and referenceWord base on
    /// feature (featureName)
    double similarity(String[] featureNames, Word proposedWord, Word referenceWord);

    String toString();

    void setReversed(boolean reversed);

    double getWeight();

    void setWeight(double w);

    void compile(Object obj);
}
