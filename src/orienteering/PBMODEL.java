package orienteering;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import orienteering.Arc;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import util.ConnectR;
public class PBMODEL {

	public static List<Arc> arcs = new LinkedList<>();

	public static double[] PB(double alpha, ConnectR connection) throws IOException, REXPMismatchException, REngineException {
        double PCV = 0, OV = 0;
        try {
			arcs.clear();

			IloCplex cplex = new IloCplex();
			cplex.setOut(null);
			cplex.setParam(IloCplex.DoubleParam.SolnPoolGap,0);
			//cplex.setParam(IloCplex.IntParam.SolnPoolIntensity,4);

			double Tmax = ReadData.Tmax;
			int n = ReadData.mNumRequests;
			double xPos[] = ReadData.xPos;
			double yPos[] = ReadData.yPos;
			double b[] = ReadData.b;
			double deviation[][] = ReadData.deviation;
			double[][] distance = ReadData.distance;
			double Max_dev = ReadData.Max_dev;

			
			//util.Graph visualization = new util.Graph(xPos, yPos);
			
			
			//variables
			//IloNumVar[] x = cplex.boolVarArray(n);
			
			IloNumVar[][] y = new IloNumVar[n][n];
			IloNumVar[][] p = new IloNumVar[n][n];
			IloNumVar[][] x = new IloNumVar[n][n];

			IloNumVar z = cplex.numVar(0,Double.MAX_VALUE);
			
			for(int i=0; i<n; i++) {
				y[i] = cplex.boolVarArray(n);
				p[i] = cplex.numVarArray(n, 0, Double.MAX_VALUE);
				x[i] = cplex.numVarArray(n,0,Double.MAX_VALUE);
			}
			
			IloNumVar[] u = cplex.numVarArray(n-1, 1, n-1);

			//objective
			IloLinearNumExpr obj = cplex.linearNumExpr();
			for(int i=1; i<n; i++) {
				//IloLinearNumExpr sumX = cplex.linearNumExpr();
				for(int j=0; j<n; j++)
					if(j!=i)
						obj.addTerm(b[i],y[i][j]);
				//obj.addTerm(1, (IloNumVar) sumX);
			}
			
			cplex.addMaximize(obj);

			//constraints
			//-1
			IloLinearNumExpr constraint, constraint1, constraint2;
			for(int i=0; i<n; i++)
			{
				for(int j=0; j<n; j++)
				{
					constraint = cplex.linearNumExpr();
					constraint1 = cplex.linearNumExpr();
					constraint2 = cplex.linearNumExpr();

					constraint2.addTerm(1, x[i][j]);
					constraint2.addTerm(-1,z);
					constraint2.addTerm(-Max_dev,y[i][j]);
					constraint1.addTerm(Max_dev,y[i][j]);
					constraint.addTerm(1, x[i][j]);
					cplex.addLe(constraint, z);
					cplex.addLe(constraint,constraint1);
					cplex.addGe(constraint2,-Max_dev);
				}
			}

			//0
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
					constraint.addTerm(alpha,x[i][j]);
				}
			}
			//constraint.addTerm(Gamma,z);
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
			int length = 0;

			if(cplex.populate()) {
				System.out.println("Solution status "+cplex.getStatus());
				System.out.println("OV " + cplex.getObjValue());
				System.out.println("Num of Sol " + cplex.getSolnPoolNsolns());
				System.out.println("Num of Sol removed " + cplex.getSolnPoolNreplaced());
				System.out.println("Mean OV " + cplex.getSolnPoolMeanObjValue());
				for (int i=0; i<cplex.getSolnPoolNsolns();i++) {
					System.out.println("OV " + i + "is " + cplex.getObjValue(i));
				}

				for(int k=0; k<cplex.getSolnPoolNsolns(); k++) {
					if(cplex.getObjValue(k) == cplex.getObjValue()) {
						for(int i=0; i<n; i++)
							for(int j=0; j<n; j++) {
								//System.out.println(cplex.getValue(y[i][j],k));
								if (Math.round(cplex.getValue(y[i][j],k)) == 1.0) {
									length++;
									System.out.println("arc:(" + i + "," + j + ") is selected!");
									//arcs.add(new Arc(i,j));
									//visualization.arc[i][j] = true;
								}
							}
					}
					System.out.println("Next sol");
				}



			}

			/*
			if(cplex.solve())
			{
				for(int i=0; i<n; i++)
					for(int j=0; j<n; j++) {
						//System.out.print("y: " + cplex.getValue(y[i][j]));
						//System.out.print("x: " + cplex.getValue(x[i][j]));
						if (Math.round(cplex.getValue(y[i][j])) == 1.0) {
							length++;
							//System.out.println("arc:(" + i + "," + j + ") is selected!");
							arcs.add(new Arc(i,j));
							//visualization.arc[i][j] = true;
						}
					}
				//System.out.println();
				//visualization.repaint();
			}

			double nominalDistance = ReadData.nominalDis(arcs);
			double[] deviations = ReadData.getDeviations(arcs, length);
			double[] value = {nominalDistance-Tmax};

            PCV = connection.connectToR(deviations,value);
            OV = cplex.getObjValue();


			System.out.println("alpha: " + alpha);
			System.out.println();
			System.out.println("Objective Value: " + OV );
			System.out.println();
			System.out.println("PCV: " + PCV );
			System.out.println();
			System.out.println("-----------------------------------------------");
			System.out.println();
*/
			//end
			cplex.end();
			
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        double[] pair = {OV,PCV};
        return pair;
	}
}
