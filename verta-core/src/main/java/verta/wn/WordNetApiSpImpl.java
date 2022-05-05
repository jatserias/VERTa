package verta.wn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;
import edu.smu.tspell.wordnet.SynsetType;

/**
 * 
 * TODO fix this should be a singleton TODO not thread safe
 *
 * 
 */
public class WordNetApiSpImpl implements WordNetAPI {
	private static Logger LOGGER = Logger.getLogger(WordNetApiSpImpl.class.getName());

	static HashMap<String, String> hypern;
	static HashMap<String, Set<SPSynset>> lemmas2synset;
	static HashMap<String, SPSynset> synset2lemmas;

	static final String variantIinputFile = "/variants.gz";

	static final String hyperIinputFile = "/hyper.gz";

	public WordNetApiSpImpl(String lang, String path) {
		try {
			load(lang, path);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		LOGGER.info("WN " + lang + " loaded! #hyper:" + hypern.size() + " #lemmas2synset:" + lemmas2synset.size()
				+ " #synset2lemmas:" + synset2lemmas.size());
	}

	static final String variant2lemma(String var) {
		return var.substring(0, var.lastIndexOf("_"));
	}

	public static void load(String lang, String path) throws IOException {
		hypern = new HashMap<>();
		lemmas2synset = new HashMap<>();
		synset2lemmas = new HashMap<>();
		LOGGER.info("Loading " + lang);
		try (BufferedReader f_variants = new BufferedReader(new InputStreamReader(
				new GZIPInputStream(new FileInputStream(path + "/" + lang + "/variants.gz")), "UTF-8"))) {
			assert f_variants != null;
			String buff;
			while ((buff = f_variants.readLine()) != null) {
				String[] ifields = buff.split("[ \t]");
				String lemma1 = variant2lemma(ifields[1]);
				String syn = ifields[0];
				SPSynset v1 = synset2lemmas.containsKey(syn) ? synset2lemmas.get(syn) : new SPSynset(syn);
				v1.add(lemma1);
				synset2lemmas.put(syn, v1);
				Set<SPSynset> s1 = (lemmas2synset.containsKey(lemma1) ? lemmas2synset.get(lemma1)
						: new HashSet<SPSynset>());
				s1.add(v1);
				lemmas2synset.put(lemma1, s1);
			}
		}
		LOGGER.info("Loaded " + lemmas2synset.size() + " lemmas");

		try (BufferedReader f_hyper = new BufferedReader(new InputStreamReader(
				new GZIPInputStream(new FileInputStream(path + "/" + lang + "/hyper.gz")), "UTF-8"))) {
			assert f_hyper != null;
			String buff;
			while ((buff = f_hyper.readLine()) != null) {
				String[] ifields = buff.split("[ \t]");
				SPSynset hyp = synset2lemmas.get(ifields[1]);
				if (hyp == null)
					continue;
				SPSynset syn = synset2lemmas.get(ifields[0]);
				if (syn != null)
					syn.addHyper(hyp);

			}
		}
	}

	public static final SPSynset[] EmptyArraySPSynset = new SPSynset[0];

	public JABSynset[] getSynsets(String wordForm, SynsetType pos) {
		Vector<SPSynset> res = new Vector<>();
		String rpos = "n";
		if (pos == SynsetType.ADJECTIVE || pos == SynsetType.ADJECTIVE_SATELLITE) {
			rpos = "a";
		} else if (pos == SynsetType.ADVERB) {
			rpos = "r";
		} else if (pos == SynsetType.VERB) {
			rpos = "v";
		}
		byte b;
		int i;
		SPSynset[] arrayOfSPSynset;
		for (i = (arrayOfSPSynset = getSynsets(wordForm)).length, b = 0; b < i;) {
			SPSynset s = arrayOfSPSynset[b];
			if (s.id.endsWith(rpos))
				res.add(s);
			b++;
		}
		return res.<JABSynset>toArray((JABSynset[]) new SPSynset[0]);
	}

	public SPSynset[] getSynsets(String wordForm) {
		if (lemmas2synset.containsKey(wordForm))
			return (SPSynset[]) lemmas2synset.get(wordForm).toArray((Object[]) EmptyArraySPSynset);
		return EmptyArraySPSynset;
	}

	public String[] getBaseFormCandidates(String wordForm, SynsetType pos) {
		throw new RuntimeException("Lemmatization is not yet implemented in SP Wordnet");
	}

	static SynsetType[] _SN = new SynsetType[] { SynsetType.NOUN };

	static SynsetType[] _SV = new SynsetType[] { SynsetType.VERB };

	static SynsetType[] _SA = new SynsetType[] { SynsetType.ADJECTIVE, SynsetType.ADJECTIVE_SATELLITE };

	static SynsetType[] _SR = new SynsetType[] { SynsetType.ADVERB };

	public SynsetType[] getSynsetTypeFromPos(String pos) {
		switch (pos.charAt(0)) {
		case 'N':
		case 'n':
			return _SN;
		case 'V':
		case 'v':
			return _SV;
		case 'A':
		case 'a':
			return _SA;
		case 'R':
		case 'r':
			return _SR;
		}
		return null;
	}
}
