package verta.wn;

import edu.smu.tspell.wordnet.SynsetType;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

/**
 * TODO fix this should be a singleton TODO not thread safe
 */
@Slf4j
public class WordNetSpImpl implements IWordNet {
    public static final SPSynset[] EmptyArraySPSynset = new SPSynset[0];

    static HashMap<String, String> hypern;
    static HashMap<String, Set<SPSynset>> lemmas2synset;
    static HashMap<String, SPSynset> synset2lemmas;
    static SynsetType[] _SN = new SynsetType[]{SynsetType.NOUN};
    static SynsetType[] _SV = new SynsetType[]{SynsetType.VERB};
    static SynsetType[] _SA = new SynsetType[]{SynsetType.ADJECTIVE, SynsetType.ADJECTIVE_SATELLITE};
    static SynsetType[] _SR = new SynsetType[]{SynsetType.ADVERB};

    public WordNetSpImpl(String lang, String path) {
        try {
            load(lang, path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("WN " + lang + " loaded! #hyper:" + hypern.size() + " #lemmas2synset:" + lemmas2synset.size() + " #synset2lemmas:" + synset2lemmas.size());
    }

    static String variant2lemma(String var) {
        return var.substring(0, var.lastIndexOf("_"));
    }

    public static void load(String lang, String path) throws IOException {
        hypern = new HashMap<>();
        lemmas2synset = new HashMap<>();
        synset2lemmas = new HashMap<>();
        log.info("Loading " + lang);
        try (BufferedReader f_variants = new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(Paths.get(path + "/" + lang + "/variants.gz"))), StandardCharsets.UTF_8))) {
            String buff;
            while ((buff = f_variants.readLine()) != null) {
                String[] ifields = buff.split("[ \t]");
                String lemma1 = variant2lemma(ifields[1]);
                String syn = ifields[0];
                SPSynset v1 = synset2lemmas.containsKey(syn) ? synset2lemmas.get(syn) : new SPSynset(syn);
                v1.add(lemma1);
                synset2lemmas.put(syn, v1);
                Set<SPSynset> s1 = (lemmas2synset.containsKey(lemma1) ? lemmas2synset.get(lemma1) : new HashSet<>());
                s1.add(v1);
                lemmas2synset.put(lemma1, s1);
            }
        }
        log.info("Loaded " + lemmas2synset.size() + " lemmas");

        try (BufferedReader f_hyper = new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(Paths.get(path + "/" + lang + "/hyper.gz"))), StandardCharsets.UTF_8))) {
            String buff;
            while ((buff = f_hyper.readLine()) != null) {
                String[] ifields = buff.split("[ \t]");
                SPSynset hyp = synset2lemmas.get(ifields[1]);
                if (hyp == null) continue;
                SPSynset syn = synset2lemmas.get(ifields[0]);
                if (syn != null) syn.addHyper(hyp);

            }
        }
    }

    public ISynset[] getSynsets(String wordForm, SynsetType pos) {
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
        for (i = (arrayOfSPSynset = getSynsets(wordForm)).length, b = 0; b < i; ) {
            SPSynset s = arrayOfSPSynset[b];
            if (s.id.endsWith(rpos)) res.add(s);
            b++;
        }
        return res.toArray(new SPSynset[0]);
    }

    public SPSynset[] getSynsets(String wordForm) {
        if (lemmas2synset.containsKey(wordForm))
            return (SPSynset[]) lemmas2synset.get(wordForm).toArray((Object[]) EmptyArraySPSynset);
        return EmptyArraySPSynset;
    }

    public String[] getBaseFormCandidates(String wordForm, SynsetType pos) {
        throw new RuntimeException("Lemmatization is not yet implemented in SP Wordnet");
    }

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
