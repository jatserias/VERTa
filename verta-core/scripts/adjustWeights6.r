##
## call with parameter reference file (csv) and the VERTa's output filename.
##
## e.g. Rscript scripts/adjustWeights.r  /Users/jordiatserias/Dropbox/eli/Batallin/segmentlevel_correlations_final.csv respes2.txt
##
##
args <- commandArgs(TRUE)

heval <- read.csv2(file=args[1],head=TRUE,sep=",")
seli <- read.csv2(file=args[2],head=FALSE,sep="\t")

##
## this should be changed if we have more modules
##
x <- cbind( seli[6], seli[13], seli[20], seli[27], seli[34], seli[41])

#Magic loop

 
max=0;smax="";
for(i in 0:100) { 
 for (j in 0: abs(100-i)) { 
  for (k in 0: abs(100-i-j))  { 
   for (m in 0: abs(100-i-j-k))  { 
    for (n in 0: abs(100-i-j-k-m))  {    
   z=100-i-j-k-m-n;
   r<- c((x[,1] * i + x[,2] * j  + x[,3] * k + x[,4] * m + x[,5] *n + x[,6]* z)* 0.06 + 1); 
   co = cor.test(r,heval$human);
   if(co$estimate>max) { smax=sprintf("MAX %s %d %d %d %d %d %d",co$estimate,i,j,k,m,n,z);print(sprintf("MAX %s %d %d %d %d %d %d",co$estimate,i,j,k,m,n,z)); max=co$estimate;}
}}}}}

print(smax);

