package mt.core;

import java.io.PrintStream;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import mt.NgramMatch;
import mt.SentenceSimilarityCountDeps;
import mt.SentenceSimilarityNERC;
import mt.SentenceSimilaritySentiment;
import mt.SentenceSimilarityTripleOverlapping;
import mt.nlp.Sentence;

// WeightedSentenceMetric 
	/// interfice to compare two sentences 
@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME, 
	include = JsonTypeInfo.As.PROPERTY, 
	property = "type")
@JsonSubTypes({ 
	@Type(value = WeightedSentenceMetric.class, name = "WeightedSentenceMetric"),
	@Type(value = NgramMatch.class, name = "NgramMatch"),
	@Type(value = NgramMatchPro.class, name = "NgramMatchPro"),
	@Type(value = SentenceSimilarityCountDeps.class, name = "SentenceSimilarityCountDeps"),
	@Type(value = SentenceSimilarityNERC.class, name = "SentenceSimilarityCountDeps"),
	@Type(value = SentenceSimilaritySentiment.class , name = "SentenceSimilarityDepScore"),
	@Type(value = SentenceSimilarityCountDeps.class, name = "SentenceSimilarityCountDeps"),
	@Type(value = SentenceSimilarityTripleOverlapping.class, name = "SentenceSimilarityTripleOverlapping"),
})

public interface ISentenceMetric {

	public SimilarityResult similarity(final Sentence source, final Sentence target, final ISentenceAlignment dist, PrintStream strace);

	public void dump(PrintStream strace);
			
}
