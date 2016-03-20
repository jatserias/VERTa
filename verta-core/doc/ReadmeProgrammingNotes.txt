General Notes:
---------------

- Code is undocumented
- Prinstream (strace) parameter is used wildly to generated the debugging xml trace while the metric is calculated (exp folder containing xml+xsl browseable files)
- The real output are several files (one per hypothesis)  containing Prec/Recall/F1 is generated (one line per segment)
- "New" version includes JSAP arguments and multiple references/ hypothesis (not commited yet)

Usage & packaging:
---------------

see README.txt

Princeton WordNet access 
------------------------

through class mt.Wordnet
uses jaws (http://lyle.smu.edu/~tspell/jaws/index.html) jwnl package could be an alternative
the wordnet data wordnet data version is controlled by defining the property -Dwordnet.database.dir=XXXX (see README.txt)
The function used are related to obtain possible lemmas from a word form, possible senses and hypernym/hyponym information

used in:

SimilarityLemma
SimilaritySynonymWn
SimilarityHypernymWn
SimilarityHyponimWn

Metrics:
--------
there are to types of module metrics:

 - lexical: basically a combination of comparing words (highly configurable through conf file and implementing mt.Similarity)
 - "structural": depend on the "alignment" of the lexical metrics. E.g. n-grams, dependency (ad-hoc no clear generalization yet, calls hardcoded in the main similarity (MTsimilarity)


Classes:
--------

mt.Similarity

basic interface for similarities at word level: double  similarity(String[] featureNames, Word proposedWord, Word referenceWord)  compares two words according to the features in the featureNames


mt.MTsimilarity:

main program, implements the combination of different modules and the sentenceSimilarity function and bestMatch function


mt.WordMetric:

A factory is used to create the corresponding metrics defined in the config file (see README.txt)


mt.SentenceSimilarityTripleOverlapping

dependency similarity measure


mt.NgramMatch

n-gram similarity measure


mt.ReaderCONLL

Reader for the NLP annotated files, it is an "extended" CONLL format (line tabulated format ). The names corresponding to the different columns are specified at conl08.fmt

