#cat("R script loaded!")

l<-length(deviations)
value <- value*-1


pcv<-0


for(p in 0:l) {
    index<-1:l
    index<-combn(index,p, FUN = function(x) replace(index,x,-1))
    index<-replace(index,index!=-1,1)
    
    result<-0
    
    for(i in 1:ncol(index)) {
        n <- sum(deviations*index[,i])
        if(value>=n) {
            result<-result+1
        }
    }
    
    
    pcv<-pcv+result
}


pcv <- 1-pcv/2^l

#cat(pcv)
