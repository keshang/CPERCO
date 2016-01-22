package orienteering;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import util.ConnectR;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class OPExtra {

	public static List<Arc> arcs = new LinkedList<>();

	public static double[] OP(double epsilon, ConnectR connection) throws IOException, REXPMismatchException, REngineException {
		double OV = 0;
		double[] PCV = {0,0,0,0,0};
		try {
			arcs.clear();

			IloCplex cplex = new IloCplex();
			cplex.setOut(null);
			cplex.setParam(IloCplex.DoubleParam.SolnPoolGap,0);
			cplex.setParam(IloCplex.IntParam.SolnPoolIntensity,4);
			cplex.setParam(IloCplex.IntParam.PopulateLim,100);

			int n = ReadData.mNumRequests;
			double xPos[] = ReadData.xPos;
			double yPos[] = ReadData.yPos;
			double Tmax = ReadData.Tmax;
			double b[] = ReadData.b;
			double deviation[][] = ReadData.deviation;
			double[][] distance = ReadData.distance;
			double theta = 0.00001;
			
			//util.Graph visualization = new util.Graph(xPos, yPos);
			

			IloNumVar[][] y = new IloNumVar[n][n];
			
			for(int i=0; i<n; i++) {
				y[i] = cplex.boolVarArray(n);
			}
			
			IloNumVar[] u = cplex.numVarArray(n-1, 1, n-1);

			//objective
			IloLinearNumExpr obj = cplex.linearNumExpr();
			//IloQuadNumExpr obj = cplex.quadNumExpr();

			for(int i=0; i<n; i++)
			{
				for(int j=0; j<n; j++)
				{
					obj.addTerm(distance[i][j], y[i][j]);
					obj.addTerm(epsilon*deviation[i][j], y[i][j]);
				}
			}

			cplex.addMinimize(obj);

			//constraints
			//1
			IloLinearNumExpr constraint, constraint1;

			constraint = cplex.linearNumExpr();
			for(int i=1; i<n; i++) {
				for(int j=0; j<n; j++)
					if(j!=i) {
						constraint.addTerm(b[i], y[i][j]);
					}
			}
			cplex.addGe(constraint,395);

			constraint = cplex.linearNumExpr();

			for(int i=0; i<n; i++)
			{
				for(int j=0; j<n; j++)
				{
					constraint.addTerm(distance[i][j], y[i][j]);
					constraint.addTerm(epsilon*deviation[i][j], y[i][j]);
				}
			}
			cplex.addLe(constraint, Tmax);
			//cplex.getSlack();


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
			if(cplex.solve())
			{
				for(int i=0; i<n; i++)
					for(int j=0; j<n; j++) {
						if (i!=j && Math.round(cplex.getValue(y[i][j])) == 1.0) {
							length++;
							arcs.add(new Arc(i,j));
						}
					}
			}
			double nominalDistance = ReadData.nominalDis(arcs);
			double[] deviations = ReadData.getDeviations(arcs, length);
			double[] value = {nominalDistance - Tmax};

			for (int i=0; i<1; i++)
				PCV[i] = connection.connectToR(deviations,value,i);
			OV = ReadData.getObjValue(arcs);


			System.out.println("epsilon: " + epsilon);
			System.out.println();
			System.out.println("Objective Value: " + OV);
			System.out.println();
			System.out.println("PCV: " + PCV[0] );
			System.out.println();
			System.out.println("-----------------------------------------------");
			System.out.println();

			//end
			cplex.end();
			
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		double[] pair = {OV,PCV[0],PCV[1],PCV[2],PCV[3],PCV[4]};
		return pair;
	}

	public static void main(String[] args) throws IOException, REXPMismatchException, REngineException {
		String file = "./data/data1.txt";
		ReadData.read(file);
		ReadData.updateDeviations(0);
		ConnectR connection = new ConnectR();
		OP(0.0100502512562814 , connection);
	}


}
