#! /bin/bash

MEMORY=3
root=.
# Linux / Mac
#if uname  | egrep '((Linux)|(Darwin))' ; then
export CLASSPATH="$(ls -1 $root/lib/*jar | paste -s -d: -)";
#else
# cygwin on windows:
#export CLASSPATH=`$root/scripts/cygjavapath.pl $root/lib/*jar`;
#fi

java -Xmx${MEMORY}G  -cp mteval.jar:${CLASSPATH} mt.EliParser   ${*}


