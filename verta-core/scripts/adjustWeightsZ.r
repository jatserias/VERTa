##
## call with parameter reference file (csv) and the VERTa's output filename.
##
## e.g. Rscript scripts/adjustWeights.r  /Users/jordiatserias/Dropbox/eli/Batallin/segmentlevel_correlations_final.csv respes2.txt
##
##
args <- commandArgs(TRUE)

heval <- read.csv2(file=args[1],head=TRUE,sep=",",stringsAsFactors=FALSE)
seli <- read.csv2(file=args[2],head=FALSE,sep="\t")

##
## this should be changed if we have more modules
##
x <- cbind( seli[6], seli[13], seli[20], seli[27])

#Magic loop
#print(heval$human)
nmin <- min(as.numeric(heval$human))
nmax <- max(as.numeric(heval$human))
print (nmin)
print (nmax)
#p1=sd(x[,1], na.rm = FALSE);
scale(x,center=TRUE,scale = TRUE); 
max=0;smax="";
for(i in 0:100) { 
 for (j in 0: abs(100-i)) { 
  for (k in 0: abs(100-i-j))  { 
   z=100-i-j-k;
   r<- c((x[,1] * i + x[,2] * j  + x[,3] * k + x[,4]* z)* 0.01 * (nmax -nmin) + nmin); 
#    print(max(r));
#    print(min(r));  
#    print(r)
    co = cor.test(r,heval$human);

   if(co$estimate>max) { smax=sprintf("MAX %s %d %d %d %d",co$estimate,i,j,k,z);print(sprintf("MAX %s %d %d %d %d",co$estimate,i,j,k,z)); max=co$estimate;}
}}}

print(smax);

