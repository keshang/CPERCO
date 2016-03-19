package orienteering;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import util.ConnectR;
public class BSMODEL {

	public static List<Arc> arcs = new LinkedList<>();
	
	public static double[] BS(double Gamma, ConnectR connection) throws IOException, REXPMismatchException, REngineException {
        double OV = 0;
		double[] PCV = {0,0,0,0,0};
        try {
			arcs.clear();

			IloCplex cplex = new IloCplex();
			cplex.setOut(null);
			cplex.setParam(IloCplex.DoubleParam.SolnPoolGap,0);
			cplex.setParam(IloCplex.IntParam.SolnPoolIntensity,4);
			cplex.setParam(IloCplex.IntParam.PopulateLim,1000000000);

			double Tmax = ReadData.Tmax;
			int n = ReadData.mNumRequests;
			double xPos[] = ReadData.xPos;
			double yPos[] = ReadData.yPos;
			double b[] = ReadData.b;
			double deviation[][] = ReadData.deviation;
			double[][] distance = ReadData.distance;

			double theta = 0.00001;
			//util.Graph visualization = new util.Graph(xPos, yPos);
			
			//variables
			//IloNumVar[] x = cplex.boolVarArray(n);
			
			IloNumVar[][] y = new IloNumVar[n][n];
			IloNumVar[][] p = new IloNumVar[n][n];

			IloNumVar z = cplex.numVar(0,Double.MAX_VALUE);
			
			for(int i=0; i<n; i++) {
				y[i] = cplex.boolVarArray(n);
				p[i] = cplex.numVarArray(n,0,Double.MAX_VALUE);
			}
			
			IloNumVar[] u = cplex.numVarArray(n-1, 1, n-1);

			//objective
			IloLinearNumExpr obj = cplex.linearNumExpr();
			for(int i=1; i<n; i++) {
				//IloLinearNumExpr sumX = cplex.linearNumExpr();
				for(int j=0; j<n; j++)
					if(j!=i) {
						obj.addTerm(b[i], y[i][j]);
						//obj.addTerm(-theta*(distance[i][j]+epsilon*deviation[i][j]), y[i][j]);
						//obj.addTerm(-theta*distance[i][j], y[i][j]);
						//obj.addTerm(-theta,p[i][j]);
					}
			}
			//obj.addTerm(-theta*Gamma,z);
			
			cplex.addMaximize(obj);

			//constraints
			//0
			IloLinearNumExpr constraint, constraint1;

			for(int i=0; i<n; i++)
			{
				for(int j=0; j<n; j++)
				{
					constraint = cplex.linearNumExpr();
					constraint.addTerm(1, p[i][j]);
					constraint.addTerm(-deviation[i][j],y[i][j]);
					constraint.addTerm(1,z);
					cplex.addGe(constraint,0);
				}
			}
			//cplex.addLe(constraint, Tmax);


			//1
			constraint = cplex.linearNumExpr();

			for(int i=0; i<n; i++)
			{
				for(int j=0; j<n; j++)
				{
					constraint.addTerm(distance[i][j], y[i][j]);
					constraint.addTerm(1,p[i][j]);
				}
			}
			constraint.addTerm(Gamma,z);
			cplex.addLe(constraint, Tmax);

			//2
			constraint = cplex.linearNumExpr();
			for(int i=1; i<n; i++)
				constraint.addTerm(1, y[i][0]);
			
			cplex.addEq(constraint, 1);
			
			constraint = cplex.linearNumExpr();
			for(int j=1; j<n; j++)
				constraint.addTerm(1, y[0][j]);
			
			cplex.addEq(constraint, 1);


			//3
			for(int j=1; j<n; j++)
			{
				constraint = cplex.linearNumExpr();
				constraint1 = cplex.linearNumExpr();
				for(int i=0; i<n; i++)
				{
					if(i!=j)
					{
						constraint.addTerm(1, y[i][j]);
						constraint1.addTerm(1, y[j][i]);
					}
				}
				//constraint.addTerm(-2,x[j]);
				cplex.addEq(constraint, constraint1);
				cplex.addLe(constraint,1);
				cplex.addLe(constraint1, 1);
			}

			//4
			for(int i=1; i<n; i++)
			{
				for(int j=1; j<n; j++)
				{
					//if(i!=j)

						constraint = cplex.linearNumExpr();
						constraint.addTerm(1, u[i-1]);
						constraint.addTerm(-1, u[j-1]);

						constraint.addTerm(n-1,y[i][j]);

						cplex.addLe(constraint, n-2);

				}
			}

			//solve
/*
			int length = 0;
			if(cplex.solve())
			{
				for(int i=0; i<n; i++)
					for(int j=0; j<n; j++) {
						//System.out.print("y: " + cplex.getValue(y[i][j]));
						//System.out.print("x: " + cplex.getValue(y[i][j]));
						if (i!=j &&Math.round(cplex.getValue(y[i][j])) == 1.0) {
							length++;
							//System.out.println("arc:(" + i + "," + j + ") is selected!");
							arcs.add(new Arc(i,j));
							//visualization.arc[i][j] = true;
						}
					}
				//System.out.println();
				//visualization.repaint();
			}
			//System.out.println("the value of z :" + cplex.getValue(z));
			double nominalDistance = ReadData.nominalDis(arcs);
			double[] deviations = ReadData.getDeviations(arcs, length);
			double[] value = {nominalDistance-Tmax};

			//util.ConnectR connection = new util.ConnectR();
			for (int i=0; i<4; i++)
				PCV[i] = connection.connectToR(deviations,value,i);
			OV = ReadData.getObjValue(arcs);
*/

			double[] MinPCV = {Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE};
			int solIndex = 0;
			if(cplex.populate()) {
				int solNum = cplex.getSolnPoolNsolns();
				double bestObj = cplex.getObjValue();
				for(int k=0; k<solNum; k++) {
					int length = 0;
					arcs.clear();
					if(cplex.getObjValue(k) == bestObj) {
						//System.out.println("OV " + cplex.getObjValue(k));
						for(int i=0; i<n; i++)
							for(int j=0; j<n; j++) {
								//System.out.println("arc " +cplex.getValue(y[i][j],k));
								if (i!=j && Math.round(cplex.getValue(y[i][j],k)) == 1.0) {
									//System.out.println("arc " + i + "," + j);
									length++;
									arcs.add(new Arc(i,j));
								}
							}

						double nominalDistance = ReadData.nominalDis(arcs);
						double[] deviations = ReadData.getDeviations(arcs, length);
						double[] value = {nominalDistance- ReadData.Tmax};

						for (int i=0; i<4; i++)
							PCV[i] = connection.connectToR(deviations,value,i);
					}

					for (int i=0; i<5; i++) {
						if (PCV[i] < MinPCV[i]) {
							MinPCV[i] = PCV[i];
						}
					}

				}
			}

			OV = cplex.getObjValue();
			PCV = MinPCV;


			System.out.println("Gamma: " + Gamma);
			System.out.println();
			System.out.println("Objective Value: " + OV);
			System.out.println();
			System.out.println("PCV: " + PCV[0] );
			System.out.println();
			System.out.println("-----------------------------------------------");
			System.out.println();


			//System.out.println("robust distance: " + orienteering.ReadData.robustDis(arcs));
			//end
			cplex.end();
			
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		double[] pair = {OV,PCV[0],PCV[1],PCV[2],PCV[3],PCV[4]};
        return pair;
	}
}
