* Prerequisites

java jdk1.6

* To install

tar xzvf mt.eval.tgz 

* To run (from the unpacked folder, cd MTmetrics)

1- MTmetric

java -cp  -Dwordnet.database.dir=<wordnet dict folder> mteval.jar:lib/jaws-bin.jar mt.MTsimilarity <metric configuration file>  <hypotesis file in conll format> <reference file in conll format> <experiment name>


2- Parser

file is one segment per line
./parseEli.sh < input  >output

e.g.: java -Dwordnet.database.dir=/usr/local/WordNet-3.0/dict -cp  mteval.jar:lib/jaws-bin.jar mt.MTsimilarity conf/metric.conf data/proposed.txt data/reference.txt provaEli

* To check the output

open exp/trace<experiment name>.xml using firefox (the deault experiment name is prova)


* To configure the application

- To configure the Input Format: edit file conf/conll08.fmt (tab separated with names of the fields)

- To configure the Metric edit file conf/metric.conf 

 Each line contains three tab separated fields: 

  1- FIELD-NAME: Must correspond to a name of the field in the input format  
  2- WEIGHT: Weights will be normalized to sum 1
  3- Similarity Function: A java class implementing the Similarity Interface
  4- (optional) use only if there is a parameter to the similarity function e.g. dictionary file, confusion matrix (See configurable metrics)

The same field-name can appear in different lines (i.e. a field can have more than one similarity function)
Lines starting with # are ignored (comments)

* Implemented similarities

mt.SimilarityEqual:  1 if values are equal 0 otherwise
mt.SimilaritySynonymDicc	1 if the words are in the dictionary file 0 otherwise. The dictionary must be passed as the 4th parameter  (conf/synonymdic.txt)
mt.SimilarityConfusionMatrix	returns the value stored in the confusionMatrix file (filename parameter must be set in the metrc.conf ) 0 if the pair of values  is not in the file
mt.SimilaritySynonymWn		1 iif source and reference are wordnet synonyms 0 otherwise (any POS)
mt.SimilaritySynonymWnPos	1 iif source and reference are wordnet synonyms 0 otherwise (using the field POS. A field named POS must exists)
mt.SimilarityHypernymWn		1 iif source is an hyponym reference in wordnet 0 otherwise

* Configurable Metrics


- mt.SimilarityHypernymWn: can be DIRECT or MULTILEVEL

- Confusion Matrix: line file <tab> separated: hypotesisLabel <tab> referenceLabel <tab> similarity 

- Synonym Dictionary: line file <tab> separated: word <tab> synonym 


* to generate a distro (VERTa-I)

cd <your workspace>
tar czvf mteval.tgz MTmetrics/mteval.jar MTmetrics/conf MTmetrics/data MTmetrics/lib MTmetrics/doc MTmetrics/xsl MTmetrics/exp MTmetrics/run.sh MTmetrics/parseEli.sh

* to generate a distro (VERTa-II)

mvn package

* Installing WordNet

create a temporal folder to work e.g. wntmp
download http://wordnetcode.princeton.edu/3.0/WordNet-3.0.tar.gz
tar xzvf WordNet-3.0.tar.gz
cd WordNet/3.0
./configure
sudo make install
#check the folder /usr/local/WordNet-3.0/ 
e.g. ls /usr/local/WordNet-3.0/ shold return bin	dict	doc	include	lib	man
# run WordNetTest
java -Dwordnet.database.dir=/usr/local/WordNet-3.0/dict -cp  mteval.jar junit.WordNetTest
# The output must look like:
....
wf:fly ball
fly: (baseball) a hit that flies up in the air) has 1 hyponyms
wf:fly
fly: fisherman's lure consisting of a fishhook decorated to look like an insect) has 1 hyponyms
....
#remove the temporal folder
rm -fr wntmp


##
## VERTa-II
##

mkdir VERTA-2.0
cd VERTA-2.0
tar xzvf  VERTa.2.0-10082012.tgz

1) per   cridar la verta (has de canviar el path de Wornet si el tens
algun altre lloc)

java -Dwordnet.database.dir=/usr/local/WordNet-3.0/dict -cp VERTa-all10082012.jar  mt.MTsimilarity   conf/dropbox.conf  eliMulti -h data/eli/100_sentences_sys01tag-new,data/eli/100_sentences_sys02tag-new,data/eli/100_sentences_sys03tag-new,data/eli/100_sentences_sys04tag-new,data/eli/100_sentences_sys05tag-new,data/eli/100_sentences_sys06tag-new,data/eli/100_sentences_sys07tag-new,data/eli/100_sentences_sys08tag-new -r data/eli/Minicorpus_100_sentences_re01tag-new,data/eli/reference02tag-new,data/eli/reference03tag-new,data/eli/reference04tag-new

(*)  Si vols generar la trac,a xml (nomes per proves petites sino t'omplira
el disc) afegeix al final -x

(*) Si vols l'antic modul de tripletes, afegeix al final -o

(*) Si vols filtrar paraules amb punctuacio inclou al final -p

(*) Si vols afegir les dependencies a TOP -t


Per veure que vol dir cada parametre: java -cp VERTa-all10082012.jar mt.MTsimilarity --help

2) ajuntar la sortida dels systems

cat exp/eliMulti_*_precrec.txt > respes.txt

3) Calcular Correlacio

si tens el R installat (sino  mira el mail o la twiki que vaig posar
com installar-lo)

Rscript scripts/correlation.r
data/eli/segmentlevel_correlations_final.csv respes.txt

El fitxer amb els judicis humans ha de tenir \n (UNIX) els cmaps separats per , i la primera linea els noms d eles columnes (human es la columna que es fa servir per calcular la correlacio)


3) Per Ajustar pesos i veure el millor resultat

Rscript scripts/adjustWeights.r
Dropbox/eli/Batallin/segmentlevel_correlations_final.csv respes.txt
