package orienteering;

import orienteering.Arc;

import java.io.*;
import java.util.List;
import java.util.Random;

/**
 * Created by shangke on 11/2/15.
 */
public class ReadData {

    public static int mNumRequests;
    public static double xPos[];
    public static double yPos[];
    public static double b[];
    public static double deviation[][];
    public static double[][] distance;
    public static final double Tmax = 40;
    public static double Max_dev;


    public static double nominalDis (List<Arc> arcs) {
        double dis = 0;
        for(Arc arc : arcs) {
            int i = arc.head;
            int j = arc.tail;
            dis += distance[i][j];
        }
        return dis;
    }

    public static double robustDis (List<Arc> arcs) {
        double dis = 0;
        for(Arc arc : arcs) {
            int i = arc.head;
            int j = arc.tail;
            dis += distance[i][j] + deviation[i][j];
        }
        return dis;
    }

    public static double getObjValue (List<Arc> arcs) {
        double obj = 0;
        for(Arc arc : arcs) {
            int i = arc.head;
            //int j = arc.tail;
            obj += b[i];
        }
        return obj;
    }

    public static double[] getDeviations(List<Arc> arcs, int length) {
        double[] deviations = new double[length];
        int index = 0;
        for(Arc arc : arcs) {
            int i = arc.head;
            int j = arc.tail;
            deviations[index] = deviation[i][j];
            index++;
        }
        return deviations;
    }



    public static void read(String input) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(new File(input)));

        int lineNumber = 0;
        String line = reader.readLine();
        while (line != null) {
            if (line.startsWith(" "))
                line = line.replaceFirst(" +", "");
            parseLine(line, lineNumber);

            line = reader.readLine();
            lineNumber++;
        }

        reader.close();

        for(int i=0; i<mNumRequests; i++)
            for(int j=0; j<mNumRequests; j++) {
                distance[i][j] = Math.sqrt(Math.pow(xPos[i] - xPos[j], 2) + Math.pow(yPos[i] - yPos[j], 2));
                //deviation[i][j] = r.nextDouble()*distance[i][j];
                //deviation[i][j] = 0.2*distance[i][j];
            }
    }

    public static void updateDeviations(long seed) {
        Random r = new Random(seed);
        for(int i=0; i<mNumRequests; i++)
            for(int j=0; j<mNumRequests; j++) {
                //deviation[i][j] = r.nextDouble()*distance[i][j];
                deviation[i][j] = 0.2*distance[i][j];
                //System.out.println(deviation[i][j]);
            }

        Max_dev = 0;
        for(int i=0; i<mNumRequests; i++) {
            for (int j = 0; j < mNumRequests; j++) {
                if (deviation[i][j] > Max_dev) {
                    Max_dev = deviation[i][j];
                }
            }
        }
    }

    private static void parseLine(String line, int lineNumber) {
        if (lineNumber == 0) {
            mNumRequests = Integer.parseInt(line);
            xPos = new double[mNumRequests];
            yPos = new double[mNumRequests];
            b = new double[mNumRequests];
            deviation = new double[mNumRequests][mNumRequests];
            distance = new double[mNumRequests][mNumRequests];
        } else if (lineNumber <= mNumRequests) {
            String[] values = line.split("\\s+");

            xPos[lineNumber-1] = Double.parseDouble(values[0]);
            yPos[lineNumber-1] = Double.parseDouble(values[1]);
            b[lineNumber-1] = Double.parseDouble(values[2]);

        }
    }

}
