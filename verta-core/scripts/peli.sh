#! /bin/bash

cat $1 | ./parseEli.sh ./src/englishPCFG.ser.gz  > $1.tag
