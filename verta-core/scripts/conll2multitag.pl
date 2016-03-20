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
     $lemma=$word;
     $pos=$fields[4];
     $dep=$fields[6];
     $label=$fields[7];
     print "$word\t$pos\t$lemma\t-\t-\t-\t-\t$dep\t$label\n";
 }
}
