#! /bin/bash

java -Dwordnet.database.dir=/usr/local/WordNet-3.0/dict -cp  mteval.jar:lib/jaws-bin.jar mt.MTsimilarity  ${*}
