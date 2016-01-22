#cat("R script loaded!")

value <- value*-1

n <- value/sum(deviations)

pcv <- 1/2-1/pi*atan(n)

pcv <- pnorm(n,mean=0,sd=1,lower=FALSE)

#cat(pcv)
