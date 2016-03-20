#! /usr/bin/perl

my $line;
my$ndoc=1;

print "FILENAME STDIN\n";
print "token   POS     morph   CONL    WNSS    WSJ     ana\n";

print "%%#DOC\t$ndoc\n";
print "%%#PAGE\t$ndoc\n";
#print "%%#Likelihood\t$ndoc\n";
$ndoc++;
while($line=<STDIN>) {
 if($line=~ /^[ \t]*$/) {
     print "%%#DOC\t$ndoc\n"; 
     print "%%#PAGE\t$ndoc\n";
    # print "%%#Likelihood\t$ndoc\n";
     $ndoc++;
   }
 else {
     @fields = split("[\t]",$line);
     $word=$fields[1];
     $lemma=$fields[2];
     $n1=$fields[4];
     $n2=$fields[5];
     $n3=$fields[6];
     $pos=$fields[3];
     $dep="-";
     $label="-";
     print "$word\t$pos\t$lemma\t$n1\t$n2\t$n3\t-\t$dep\t$label\n";
 }
}
