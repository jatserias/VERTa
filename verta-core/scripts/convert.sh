#!/bin/bash

gawk 'BEGIN {print "%%#SEC\t1";NSEG=2;} /^$/ { print "\n%%#SEC "NSEG;NSEG++;next} {print $0}'


