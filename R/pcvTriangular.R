#cat("R script loaded!")

deviations1 <- rep(deviations,2)/2


l<-length(deviations1)

pcv<-0


for(p in 0:l) {
    index<-1:l
    index<-combn(index,p, FUN = function(x) replace(index,x,-1))
    index<-replace(index,index!=-1,1)
    
    result<-0
    for(i in 1:ncol(index)) {
        result<-(max(sum(deviations1*index[,i]) + value,0))^l + result
    }

    result<-result*(-1)^p
    pcv<-pcv+result
}


pcv <- pcv/(factorial(l)*(2^l)*prod(deviations1))

#cat(pcv)
