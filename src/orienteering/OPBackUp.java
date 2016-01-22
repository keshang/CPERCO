package orienteering;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.io.IOException;
import util.ConnectR;
import util.Graph;
public class OPBackUp {
	
	public static void main(String[] args) throws IOException {
		try {

			System.out.println("hello ??");
			
			IloCplex cplex = new IloCplex();

			String file = "./data/data3.txt";
			ReadData.read(file);


			double Tmax = 80;

			int n = ReadData.mNumRequests;
			double xPos[] = ReadData.xPos;
			double yPos[] = ReadData.yPos;
			double b[] = ReadData.b;
			double[][] distance = ReadData.distance;
			
			Graph visualization = new Graph(xPos, yPos);
			

			IloNumVar[][] y = new IloNumVar[n][n];
			
			for(int i=0; i<n; i++) {
				y[i] = cplex.boolVarArray(n);
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
			//1
			IloLinearNumExpr constraint, constraint1;

			constraint = cplex.linearNumExpr();

			for(int i=0; i<n; i++)
			{
				for(int j=0; j<n; j++)
				{
					constraint.addTerm(distance[i][j], y[i][j]);
				}
			}
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
			if(cplex.solve())
			{
				for(int i=0; i<n; i++)
					for(int j=0; j<n; j++) {
						//System.out.println(cplex.getValue(y[i][j]));
						if (Math.round(cplex.getValue(y[i][j])) == 1.0) {
							//System.out.println("arc:(" + i + "," + j + ") is selected!");
							visualization.arc[i][j] = true;
						}
					}
							
				visualization.repaint();
			}
			//cplex.exportModel("model.lp");

			//end
			cplex.end();
			
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
