package util; /**
 * Created by shangke on 11/3/15.
 */

import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.*;

import java.io.*;


public class ConnectR {

    public RConnection c;

    public ConnectR() throws RserveException {
        c = new RConnection();
        if(c.isConnected()) {
            c.eval("library('distr')");
            //c.eval("cat(\"Don't close this window!! It is listening the request of the running experiment from Java!!\")");
        }
    }

    public void closeConnectR() {
        c.close();
    }

    public double connectToR(double[] deviations, double[] value, int index) throws REngineException, REXPMismatchException, IOException {
        //RConnection c = new RConnection();
        REXP x = null;
        if(c.isConnected()) {
            //c.shutdown();
            System.out.println("Connected to RServe.");
            //c.eval("library('distr')");
            c.assign("deviations", deviations);
            c.assign("value", value);
            //c.eval("U<-0");
            //c.eval("for(deviate in deviations){U<-Unif(-deviate,deviate)+U}");
            //c.eval("for(deviate in deviations){U<-2*(Beta(2,2)-0.5)*deviate + U}");
            //c.eval("for(deviate in deviations){U<-Arcsine()*deviate + U}");
            //x = c.eval("p(U)(value)");
            switch (index) {
                case 0: {//Uniform
                    c.eval("source(\"E:/sk/Cplex/R/pcv2.R\")");
                    x = c.eval("pcv");
                    break;
                }
                case 1: {//Triangular
                    //c.eval("source(\"E:/sk/Cplex/R/pcvTriangular.R\")");
                    //x = c.eval("pcv");
                    c.eval("U<-0");
                    c.eval("deviations <- rep(deviations,2)/2");
                    c.eval("for(deviate in deviations){U<-Unif(-deviate,deviate)+U}");
                    x = c.eval("p(U)(value)");
                    break;
                }
                case 2: {//Rademacher
                    c.eval("source(\"E:/sk/Cplex/R/pcvBinomial.R\")");
                    x = c.eval("pcv");
                    break;
                }
                case 3: {//Arcsine
                    c.eval("U<-0");
                    c.eval("for(deviate in deviations){U<-Arcsine()*deviate + U}");
                    x = c.eval("p(U)(value)");
                    break;
                }
                case 4: {//Beta
                    c.eval("U<-0");
                    //c.eval("b<-(Binom(prob=0.5,size=10)-5)/5");
                    c.eval("for(deviate in deviations){U<-(Beta(6,6)-.5)*deviate+U}");
                    x = c.eval("p(U)(value)");
                    //x = c.eval("pcv");
                    break;
                }
                case 5: {//Normal
                    c.eval("source(\"E:/sk/Cplex/R/pcvNormal.R\")");
                    x = c.eval("pcv");
                    break;
                }
                case 6: {//Cauchy
                    c.eval("source(\"E:/sk/Cplex/R/pcvCauchy.R\")");
                    x = c.eval("pcv");
                    break;
                }
                default: {
                    x = c.eval("pcv");
                    break;
                }
            }

        } else {
            System.out.println("Rserve could not connect");
        }
        //c.shutdown();
        //c.close();

        //System.out.println("Session Closed");
        return x.asDouble();
    }

    public void transferDataToR(double[] param, double[] OV, double[][] PCV, String file) throws REngineException {
        if(c.isConnected()) {
            c.assign("param", param);
            c.assign("ov", OV);
            c.assign("pcv1", PCV[0]);
            c.assign("pcv2", PCV[1]);
            c.assign("pcv3", PCV[2]);
            c.assign("pcv4", PCV[3]);
            c.assign("pcv5", PCV[4]);
            c.assign("thefile", file);
            c.eval("results<-data.frame(PARAM=param,OV=ov,PCV1=pcv1,PCV2=pcv2,PCV3=pcv3,PCV4=pcv4,PCV5=pcv5)");

            c.eval("write.table(results, file = thefile, quote = TRUE, sep = \" \"," +
                    "eol = \"\\n\", dec = \".\", row.names = FALSE,\n" +
                    "col.names = TRUE)");
        } else {
            System.out.println("Rserve could not connect");
        }

    }

    public void saveFile(double[] x, double[] y, double[] b, String file) throws REngineException {
        if(c.isConnected()) {
            c.eval("id<-0:19");
            c.assign("x", x);
            c.assign("y", y);
            c.assign("b", b);
            c.assign("thefile", file);
            c.eval("results<-data.frame(ID=id,X=x,Y=y,B=b)");

            c.eval("write.table(results, file = thefile, quote = TRUE, sep = \" \"," +
                    "eol = \"\\n\", dec = \".\", row.names = FALSE,\n" +
                    "col.names = TRUE)");
        } else {
            System.out.println("Rserve could not connect");
        }

    }

    public static void main(String[] args) throws REXPMismatchException, REngineException, IOException {
        double[] a = {Double.NaN};
        double[] b = {3,4,5,6};
        //double[] c = {11.1,12.3,22.1,9.9};

        ConnectR connectR = new ConnectR();
        REXP x = null;

        //connectR.c.assign("thefile", "/Users/shangke/Research/SCI for XJTU/BS_results.txt");
        connectR.c.assign("a",a);
        connectR.c.eval("print(a)");

        //connectR.c.eval("write.table(a, file = thefile, quote = TRUE, sep = \" \"," +
        //        "eol = \"\\n\", dec = \".\", row.names = FALSE,\n" +
        //        "col.names = TRUE)");

        //connectR.transferDataToR(a,b,c);

        connectR.closeConnectR();


        //System.out.println(x.asDoubles().length);
    }
}
