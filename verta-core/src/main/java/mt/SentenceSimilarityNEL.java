package mt;

import java.io.PrintStream;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;

import mt.core.MetricActivationCounter;
import mt.core.ISentenceAlignment;
import mt.core.SentenceSimilarityBase;
import mt.core.SimilarityResult;
import mt.nlp.Sentence;

public class SentenceSimilarityNEL extends SentenceSimilarityBase {

	public SentenceSimilarityNEL(MetricActivationCounter counters) {
		super(counters);
	}

	@Override
	public SimilarityResult similarity(Sentence s1, Sentence s2, ISentenceAlignment dist, PrintStream strace) {

		@SuppressWarnings("unchecked")
		Collection<String> sres = CollectionUtils.intersection(s1.getNel(), s2.getNel());

		// @TODO add matching or not matching info
		if (strace != null) {
			strace.println("<nel>");
			strace.println("<src>");
			for (String e1 : s1.getNel()) {

				strace.println("<ne>" + e1 + "</ne>");

			}
			strace.println("</src>");
			strace.println("<trg>");
			for (String e1 : s2.getNel()) {

				strace.println("<ne>" + e1 + "</ne>");

			}
			strace.println("</trg>");
			strace.println("</nel>");
			//
		}
		// TODO fix counters
		//
		getCounters().increase(this.getClass().getCanonicalName(), 1);
		return new SimilarityResult(sres.size(), s1.getNel().size(), s2.getNel().size());
	}

	@Override
	public void dump(PrintStream strace) {
	}

}
