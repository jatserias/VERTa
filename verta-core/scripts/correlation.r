##
## call with parameter reference file (csv) and the VERTa's output filename.
##
## e.g. Rscript scripts/adjustWeights.r  /Users/jordiatserias/Dropbox/eli/Batallin/segmentlevel_correlations_final.csv respes2.txt
##
##
options(digits=10)
args <- commandArgs(TRUE)

old <- read.csv2(file=args[1],head=TRUE,sep=",")
seli <- read.csv2(file=args[2],head=FALSE,sep="\t")
nmax <- max(as.double(old$human)) ## <- as.integer(args[3]); 
nmin <- min(as.double(old$human));

#print(nmax)
#print (nmin)

x <- cbind(seli[1])
r <- x[,1] * (nmax - nmin) + nmin;


#print(as.double(r[1]));
#print(as.double(old$human[1]));
print(cor.test(r,as.double(old$human)))


	

