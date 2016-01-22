#cat("R script loaded!")

value <- value*-1

n <- value/sqrt(sum(deviations^2))

pcv <- pnorm(n,mean=0,sd=1,lower=FALSE)

#cat(pcv)
