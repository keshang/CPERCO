package eprodemo;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import productionplanning.BNPP;
import productionplanning.BSPP;
import productionplanning.PP;
import util.ConnectR;

import java.io.IOException;


/**
 * Created by shangke on 11/3/15.
 */
public class main {

    public static void main(String[] args) throws IOException, REXPMismatchException, REngineException {

        //KPData.generate();
        ConnectR connection = new ConnectR();
        int mNum = 6;
        int distributionNum = 5;
        int num = 11;
        double[] Gamma = new double[num];
        double[] rho = new double[num];
        double[] rsDe = new double[num];

        for (int i = 0; i < Gamma.length; i++) {
            //Gamma[i] = (double) i / (Gamma.length - 1) * 20;
            //rho[i] = (double) i / (Gamma.length - 1)* 10;
            //rsDe[i] = (double) i / (Gamma.length - 1) * 10;

            Gamma[i] = (double) i / (Gamma.length - 1) * 2;
            rho[i] = (double) i / (Gamma.length - 1)* 1.5;
            rsDe[i] = (double) i / (Gamma.length - 1);
        }
        //Gamma[0] = 20;
        //alpha[0] = 1;
        //rho[0] = 6;

        double[] BSparam = new double[Gamma.length];
        double[] BSOV = new double[Gamma.length];
        double[][] BSPCV = new double[distributionNum][Gamma.length];


        double[] BNparam = new double[rho.length];
        double[] BNOV = new double[rho.length];
        double[][] BNPCV = new double[distributionNum][rho.length];

        double[] KPparam = new double[rho.length];
        double[] KPOV = new double[rho.length];
        double[][] KPPCV = new double[distributionNum][rho.length];

        String head = "E:\\sk\\Cplex\\results\\";
        String tail = ".txt";

        double[] pair;
        int zeroCount = 0;

        for (int seed = 0; seed < 1; seed++) {

            for (int i = 0; i < Gamma.length; i++) {
                BSparam[i] = Double.NaN;
                BSOV[i] = Double.NaN;


                BNparam[i] = Double.NaN;
                BNOV[i] = Double.NaN;

                KPparam[i] = Double.NaN;
                KPOV[i] = Double.NaN;

                for (int j=0; j<distributionNum; j++) {
                    BSPCV[j][i] = Double.NaN;
                    BNPCV[j][i] = Double.NaN;
                    KPPCV[j][i] = Double.NaN;
                }
            }

            String BSFile = head + "model1_" + seed + tail;
            String BNFile = head + "model2_" + seed + tail;
            String KPFile = head + "model_" + seed + tail;



            zeroCount = 0;
            for (int i = 0; i < Gamma.length; i++) {
                pair = model1.BS(Gamma[i], connection);
                BSparam[i] = Gamma[i];
                BSOV[i] = pair[0];
                for (int j=0; j<distributionNum; j++) {
                    BSPCV[j][i] = pair[j+1];
                }

                //break;
            }
            connection.transferDataToR(BSparam, BSOV, BSPCV, BSFile);



            zeroCount = 0;
            for (int i = 0; i < rho.length; i++) {
                pair = model2.BN(rho[i], connection);
                BNparam[i] = rho[i];
                BNOV[i] = pair[0];
                for (int j=0; j<distributionNum; j++) {
                    BNPCV[j][i] = pair[j+1];
                }

                //break;
            }
            connection.transferDataToR(BNparam, BNOV, BNPCV, BNFile);



            zeroCount = 0;
            for (int i = 0; i < rsDe.length; i++) {
                pair = model.PP(rsDe[i], connection);
                KPparam[i] = rsDe[i];
                KPOV[i] = pair[0];
                for (int j=0; j<distributionNum; j++) {
                    KPPCV[j][i] = pair[j+1];
                }
                //break;
            }
            connection.transferDataToR(KPparam, KPOV, KPPCV, KPFile);

/*
            zeroCount = 0;
            for (int i = 0; i < Gamma.length; i++) {
                pair = model11.BS(Gamma[i], connection);
                BSparam[i] = Gamma[i];
                BSOV[i] = pair[0];
                for (int j=0; j<distributionNum; j++) {
                    BSPCV[j][i] = pair[j+1];
                }
            }
            connection.transferDataToR(BSparam, BSOV, BSPCV, BSFile);



            zeroCount = 0;
            for (int i = 0; i < rho.length; i++) {
                pair = model22.BN(rho[i], connection);
                BNparam[i] = rho[i];
                BNOV[i] = pair[0];
                for (int j=0; j<distributionNum; j++) {
                    BNPCV[j][i] = pair[j+1];
                }
            }
            connection.transferDataToR(BNparam, BNOV, BNPCV, BNFile);
*/

        }
        connection.closeConnectR();
    }
}
