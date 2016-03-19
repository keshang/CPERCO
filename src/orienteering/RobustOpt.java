package orienteering;

import ilog.cplex.IloCplex;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import orienteering.OP;
import orienteering.ReadData;

import java.io.IOException;
import util.ConnectR;

/**
 * Created by shangke on 11/3/15.
 */
public class RobustOpt {

    public static void main(String[] args) throws IOException, REXPMismatchException, REngineException {
        String file = "./data/data1.txt";
        ReadData.read(file);
        ConnectR connection = new ConnectR();
        //connection.saveFile(ReadData.xPos,ReadData.yPos, ReadData.b,"E:\\sk\\Cplex\\data.txt");

        int num = 500;
        int distributionNum = 5;
        /*
        double[] Gamma = new double[num];
        double[] rho = new double[num];
        double[] rsDe = new double[num];

        for (int i = 0; i < Gamma.length; i++) {
            Gamma[i] = (double) i / (Gamma.length - 1) * 14;
            rho[i] = (double) i / (Gamma.length - 1)* 7.5;
            rsDe[i] = (double) i / (Gamma.length - 1) * 2.3;
        }
        */

        double[] Gamma = {0,0.058,0.977,1.907,2.372,2.86,3.162,3.627,5.045,5.812,7.718,7.881};
        double[] rho = {0,0.261,0.454,0.87,1.063,1.249,1.28,1.485,1.808,2.026,2.473,2.504};
        double[] rsDe = {0,0.073,0.129,0.253,0.313,0.376,0.439,0.509,0.574,0.698,0.72};

        double[] BSparam = new double[Gamma.length];
        double[] BSOV = new double[Gamma.length];
        double[][] BSPCV = new double[distributionNum][Gamma.length];

        double[] BNparam = new double[rho.length];
        double[] BNOV = new double[rho.length];
        double[][] BNPCV = new double[distributionNum][rho.length];

        double[] OPparam = new double[rsDe.length];
        double[] OPOV = new double[rsDe.length];
        double[][] OPPCV = new double[distributionNum][rsDe.length];

        String head = "E:\\sk\\Cplex\\results\\";
        String tail = ".txt";

        double[] pair;
        int zeroCount = 0;

        for (int seed = 1; seed < 2; seed++) {

            for (int i = 0; i < Gamma.length; i++) {
                BSparam[i] = Double.NaN;
                BSOV[i] = Double.NaN;
                for (int j=0; j<distributionNum; j++) {
                    BSPCV[j][i] = Double.NaN;
                }
            }
            for (int i = 0; i < rho.length; i++) {
                BNparam[i] = Double.NaN;
                BNOV[i] = Double.NaN;
                for (int j=0; j<distributionNum; j++) {
                    BNPCV[j][i] = Double.NaN;
                }
            }
            for (int i = 0; i < rsDe.length; i++) {
                OPparam[i] = Double.NaN;
                OPOV[i] = Double.NaN;
                for (int j=0; j<distributionNum; j++) {
                    OPPCV[j][i] = Double.NaN;
                }
            }


            ReadData.updateDeviations(seed);

            String BSFile = head + "OPBS_" + seed + tail;
            String BNFile = head + "OPBN_" + seed + tail;
            String OPFile = head + "OPOP_" + seed + tail;

/*
            zeroCount = 0;
            //rsDe[0] = 0.9;
            for (int i = 0; i < rsDe.length; i++) {
                pair = OP.OP(rsDe[i], connection);
                OPparam[i] = rsDe[i];
                OPOV[i] = pair[0];
                for (int j=0; j<distributionNum; j++) {
                    OPPCV[j][i] = pair[j+1];
                }
                //break;
            }
            connection.transferDataToR(OPparam, OPOV, OPPCV, OPFile);

            zeroCount = 0;
            //Gamma[0] = 11.6;
            for (int i = 0; i < Gamma.length; i++) {
                pair = orienteering.BSMODEL.BS(Gamma[i], connection);
                BSparam[i] = Gamma[i];
                BSOV[i] = pair[0];
                for (int j=0; j<distributionNum; j++) {
                    BSPCV[j][i] = pair[j+1];
                }
               // break;
            }
            connection.transferDataToR(BSparam, BSOV, BSPCV, BSFile);
*/
            zeroCount = 0;
            //rho[0] = 3.2;
            for (int i = 0; i < rho.length; i++) {
                pair = orienteering.BNMODEL.BN(rho[i], connection);
                BNparam[i] = rho[i];
                BNOV[i] = pair[0];
                for (int j=0; j<distributionNum; j++) {
                    BNPCV[j][i] = pair[j+1];
                }
                //break;
            }
            connection.transferDataToR(BNparam, BNOV, BNPCV, BNFile);

/*
            zeroCount = 0;
            for (int i = 0; i < Gamma.length; i++) {
                pair = orienteering.OP1.OP1(Gamma[i], connection);
                BSparam[i] = Gamma[i];
                BSOV[i] = pair[0];
                for (int j=0; j<distributionNum; j++) {
                    BSPCV[j][i] = pair[j+1];
                }

            }
            connection.transferDataToR(BSparam, BSOV, BSPCV, BSFile);


            //rho[0]=15;
            zeroCount = 0;
            for (int i = 0; i < rho.length; i++) {
                pair = orienteering.OP2.OP2(rho[i], connection);
                BNparam[i] = rho[i];
                BNOV[i] = pair[0];
                for (int j=0; j<distributionNum; j++) {
                    BNPCV[j][i] = pair[j+1];
                }
                //break;
            }
            connection.transferDataToR(BNparam, BNOV, BNPCV, BNFile);
*/
        }
        connection.closeConnectR();

    }
}
