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

        int num = 60;
        int distributionNum = 5;
        double[] Gamma = new double[num];
        double[] rho = new double[num];
        double[] rsDe = new double[num];

        for (int i = 0; i < Gamma.length; i++) {
            Gamma[i] = (double) i / 10000+2.67306532663317;
            rho[i] = (double) i / (Gamma.length - 1)*6.36/3+8.5;
            rsDe[i] = (double) i / 1000+2.61306532663317  ;
        }
        //Gamma[0] = 20;
        //alpha[0] = 1;
        //rho[0] = 3;

        double[] BSparam = new double[Gamma.length];
        double[] BSOV = new double[Gamma.length];
        double[][] BSPCV = new double[distributionNum][Gamma.length];

        double[] BNparam = new double[rho.length];
        double[] BNOV = new double[rho.length];
        double[][] BNPCV = new double[distributionNum][rho.length];

        double[] OPparam = new double[rho.length];
        double[] OPOV = new double[rho.length];
        double[][] OPPCV = new double[distributionNum][rho.length];

        String head = "E:\\sk\\Cplex\\results\\";
        String tail = ".txt";

        double[] pair;
        int zeroCount = 0;

        for (int seed = 7; seed < 7; seed++) {

            for (int i = 0; i < Gamma.length; i++) {
                BSparam[i] = Double.NaN;
                BSOV[i] = Double.NaN;

                BNparam[i] = Double.NaN;
                BNOV[i] = Double.NaN;

                OPparam[i] = Double.NaN;
                OPOV[i] = Double.NaN;

                for (int j=0; j<distributionNum; j++) {
                    BSPCV[j][i] = Double.NaN;
                    BNPCV[j][i] = Double.NaN;
                    OPPCV[j][i] = Double.NaN;
                }
            }

            ReadData.updateDeviations(seed);

            String BSFile = head + "OPBS_" + seed + tail;
            String BNFile = head + "OPBN_" + seed + tail;
            String OPFile = head + "OPOP_" + seed + tail;

/*
            zeroCount = 0;
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
            for (int i = 0; i < Gamma.length; i++) {
                pair = orienteering.BSMODEL.BS(Gamma[i], connection);
                BSparam[i] = Gamma[i];
                BSOV[i] = pair[0];
                for (int j=0; j<distributionNum; j++) {
                    BSPCV[j][i] = pair[j+1];
                }
            }
            connection.transferDataToR(BSparam, BSOV, BSPCV, BSFile);


            zeroCount = 0;
            for (int i = 0; i < rho.length; i++) {
                pair = orienteering.BNMODEL.BN(rho[i], connection);
                BNparam[i] = rho[i];
                BNOV[i] = pair[0];
                for (int j=0; j<distributionNum; j++) {
                    BNPCV[j][i] = pair[j+1];
                }
            }
            connection.transferDataToR(BNparam, BNOV, BNPCV, BNFile);

*/
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

/*
            rho[0]=15;
            zeroCount = 0;
            for (int i = 0; i < rho.length; i++) {
                pair = orienteering.OP2.OP2(rho[i], connection);
                BNparam[i] = rho[i];
                BNOV[i] = pair[0];
                for (int j=0; j<distributionNum; j++) {
                    BNPCV[j][i] = pair[j+1];
                }
                break;
            }
            connection.transferDataToR(BNparam, BNOV, BNPCV, BNFile);
*/
        }
        connection.closeConnectR();

    }
}
