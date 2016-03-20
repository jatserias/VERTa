#! /bin/bash

echo "wget is needed to dowload the jar"
echo "Check the license terms of JAWS at http://lyle.smu.edu/~tspell/jaws/index.html" 
wget http://lyle.smu.edu/%7Etspell/jaws/jaws-bin.jar -O jaws-bin.jar
echo "jar downloaded"
mvn install:install-file -Dfile=./jaws-bin.jar -DgroupId=jaws  -DartifactId=jaws -Dversion=1.2 -Dpackaging=jar
echo "jar installed"
